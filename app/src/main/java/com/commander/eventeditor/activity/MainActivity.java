package com.commander.eventeditor.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.commander.eventeditor.R;
import com.commander.eventeditor.adapter.EventListAdapter;
import com.commander.eventeditor.model.Event;
import com.commander.eventeditor.util.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 1001;

    // File upload
    private LinearLayout fileUploadArea, fileInfo, eventDetails;
    private TextView tvFileName, tvEventCount;
    private TextView detailId, detailTrigger, detailTriggerValue, detailChance;
    private Button btnSelectFile, btnLoadEvent;

    // Event list
    private RecyclerView rvEventList;
    private EventListAdapter eventAdapter;
    private List<Event> eventData = new ArrayList<>();
    private int selectedEventIndex = -1;

    // Form fields
    private EditText etId, etConquerId;
    private EditText etEventBuffId1, etRound1, etCountryId1;
    private EditText etEventBuffId2, etRound2, etCountryId2;
    private EditText etTrigger, etTriggerValueStr, etLocation, etChance;
    private TextView tvTriggerInfo;

    // Template
    private EditText etTemplateSearch;
    private Button btnTemplateRound, btnTemplateConquer, btnTemplateAttack, btnTemplateGeneral;

    // Action buttons
    private Button btnReset, btnGenerate;

    // Output
    private TextView tvOutput;
    private Button btnCopy, btnFormat, btnMinify, btnExport;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
        setupEventList();

        // Initial output
        generateJson();
        addToEventList(createDefaultEvent());
    }

    private void initViews() {
        // File upload
        fileUploadArea = findViewById(R.id.fileUploadArea);
        fileInfo = findViewById(R.id.fileInfo);
        tvFileName = findViewById(R.id.tvFileName);
        tvEventCount = findViewById(R.id.tvEventCount);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        rvEventList = findViewById(R.id.rvEventList);
        eventDetails = findViewById(R.id.eventDetails);
        btnLoadEvent = findViewById(R.id.btnLoadEvent);

        detailId = findViewById(R.id.detailId);
        detailTrigger = findViewById(R.id.detailTrigger);
        detailTriggerValue = findViewById(R.id.detailTriggerValue);
        detailChance = findViewById(R.id.detailChance);

        // Form
        etId = findViewById(R.id.etId);
        etConquerId = findViewById(R.id.etConquerId);
        etEventBuffId1 = findViewById(R.id.etEventBuffId1);
        etRound1 = findViewById(R.id.etRound1);
        etCountryId1 = findViewById(R.id.etCountryId1);
        etEventBuffId2 = findViewById(R.id.etEventBuffId2);
        etRound2 = findViewById(R.id.etRound2);
        etCountryId2 = findViewById(R.id.etCountryId2);
        etTrigger = findViewById(R.id.etTrigger);
        etTriggerValueStr = findViewById(R.id.etTriggerValueStr);
        etLocation = findViewById(R.id.etLocation);
        etChance = findViewById(R.id.etChance);
        tvTriggerInfo = findViewById(R.id.tvTriggerInfo);

        // Templates
        etTemplateSearch = findViewById(R.id.etTemplateSearch);
        btnTemplateRound = findViewById(R.id.btnTemplateRound);
        btnTemplateConquer = findViewById(R.id.btnTemplateConquer);
        btnTemplateAttack = findViewById(R.id.btnTemplateAttack);
        btnTemplateGeneral = findViewById(R.id.btnTemplateGeneral);

        // Actions
        btnReset = findViewById(R.id.btnReset);
        btnGenerate = findViewById(R.id.btnGenerate);

        // Output
        tvOutput = findViewById(R.id.tvOutput);
        btnCopy = findViewById(R.id.btnCopy);
        btnFormat = findViewById(R.id.btnFormat);
        btnMinify = findViewById(R.id.btnMinify);
        btnExport = findViewById(R.id.btnExport);
    }

    private void setupListeners() {
        // File upload
        fileUploadArea.setOnClickListener(v -> selectFile());
        btnSelectFile.setOnClickListener(v -> selectFile());

        // Load event to editor
        btnLoadEvent.setOnClickListener(v -> loadSelectedEvent());

        // Template buttons
        btnTemplateRound.setOnClickListener(v -> loadTemplate(createRoundTemplate()));
        btnTemplateConquer.setOnClickListener(v -> loadTemplate(createConquerTemplate()));
        btnTemplateAttack.setOnClickListener(v -> loadTemplate(createAttackTemplate()));
        btnTemplateGeneral.setOnClickListener(v -> loadTemplate(createGeneralTemplate()));

        // Template search
        etTemplateSearch.setOnEditorActionListener((v, actionId, event) -> {
            filterTemplates();
            return false;
        });

        // Trigger type change listener
        etTrigger.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updateTriggerInfo();
            }
        });

        // Action buttons
        btnReset.setOnClickListener(v -> resetForm());
        btnGenerate.setOnClickListener(v -> {
            if (validateForm()) {
                generateJson();
                showToast("JSON生成成功");
            } else {
                showToast("请检查表单中的错误");
            }
        });

        // Output buttons
        btnCopy.setOnClickListener(v -> copyToClipboard());
        btnFormat.setOnClickListener(v -> formatJson());
        btnMinify.setOnClickListener(v -> minifyJson());
        btnExport.setOnClickListener(v -> exportJson());

        // Auto-generate on field change
        View.OnFocusChangeListener autoGenerate = (v, hasFocus) -> {
            if (!hasFocus) {
                generateJson();
            }
        };
        etId.setOnFocusChangeListener(autoGenerate);
        etTrigger.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updateTriggerInfo();
                generateJson();
            }
        });
    }

    private void setupEventList() {
        eventAdapter = new EventListAdapter(eventData, new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event, int position) {
                selectedEventIndex = position;
                showEventDetails(event);
            }

            @Override
            public void onDeleteClick(Event event, int position) {
                eventData.remove(position);
                if (position == selectedEventIndex) {
                    selectedEventIndex = -1;
                    eventDetails.setVisibility(View.GONE);
                } else if (selectedEventIndex > position) {
                    selectedEventIndex--;
                }
                eventAdapter.updateData(eventData);
                updateEventCount();
            }
        });
        rvEventList.setLayoutManager(new LinearLayoutManager(this));
        rvEventList.setAdapter(eventAdapter);
    }

    // ============ File Operations ============

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == FILE_SELECT_CODE && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                loadJsonFile(uri);
            }
        }
    }

    private void loadJsonFile(Uri uri) {
        try {
            List<Event> parsed = JsonHelper.loadEventsFromUri(this, uri);

            if (parsed.isEmpty()) {
                showToast("无效的JSON数据或未找到事件");
                return;
            }

            eventData = parsed;
            selectedEventIndex = -1;

            // Update UI
            String fileName = getFileName(uri);
            tvFileName.setText(fileName);
            fileInfo.setVisibility(View.VISIBLE);
            eventDetails.setVisibility(View.GONE);

            updateEventCount();
            eventAdapter.updateData(eventData);
            showToast("文件加载成功: " + parsed.size() + "个事件");
        } catch (Exception e) {
            showToast("解析失败: " + e.getMessage());
        }
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        return path != null ? path.substring(path.lastIndexOf('/') + 1) : "未知文件";
    }

    // ============ Event List ============

    private void showEventDetails(Event event) {
        eventDetails.setVisibility(View.VISIBLE);
        detailId.setText("事件ID: " + event.getId());
        detailTrigger.setText("触发类型: " + Event.getTriggerName(event.getTrigger()));
        detailTriggerValue.setText("触发值: " + formatIntList(event.getTriggerValue()));
        detailChance.setText("触发概率: " + event.getChance() + "%");
    }

    private void loadSelectedEvent() {
        if (selectedEventIndex >= 0 && selectedEventIndex < eventData.size()) {
            populateForm(eventData.get(selectedEventIndex));
            showToast("事件已加载到编辑器");
        } else {
            showToast("请先选择一个事件");
        }
    }

    private void addToEventList(Event event) {
        // Check if event ID already exists
        for (Event e : eventData) {
            if (e.getId() == event.getId()) return; // Skip duplicates from initial load
        }
        eventData.add(event);
        eventAdapter.updateData(eventData);
        updateEventCount();
    }

    private void updateEventCount() {
        tvEventCount.setText(eventData.size() + "个事件");
    }

    // ============ Form Operations ============

    private Event collectForm() {
        Event event = new Event();
        event.setId(getInt(etId));
        event.setConquerId(getInt(etConquerId));
        event.setEventBuffId1(getInt(etEventBuffId1));
        event.setRound1(getInt(etRound1));
        event.setCountryId1(parseIntList(etCountryId1.getText().toString()));
        event.setEventBuffId2(getInt(etEventBuffId2));
        event.setRound2(getInt(etRound2));
        event.setCountryId2(parseIntList(etCountryId2.getText().toString()));
        event.setTrigger(getInt(etTrigger));
        event.setTriggerValue(parseIntList(etTriggerValueStr.getText().toString()));
        event.setLocation(parseIntList(etLocation.getText().toString()));
        event.setChance(getInt(etChance));
        return event;
    }

    private void populateForm(Event event) {
        etId.setText(String.valueOf(event.getId()));
        etConquerId.setText(String.valueOf(event.getConquerId()));
        etEventBuffId1.setText(String.valueOf(event.getEventBuffId1()));
        etRound1.setText(String.valueOf(event.getRound1()));
        etCountryId1.setText(formatIntList(event.getCountryId1()));
        etEventBuffId2.setText(String.valueOf(event.getEventBuffId2()));
        etRound2.setText(String.valueOf(event.getRound2()));
        etCountryId2.setText(formatIntList(event.getCountryId2()));
        etTrigger.setText(String.valueOf(event.getTrigger()));
        etTriggerValueStr.setText(formatIntList(event.getTriggerValue()));
        etLocation.setText(formatIntList(event.getLocation()));
        etChance.setText(String.valueOf(event.getChance()));

        updateTriggerInfo();
        generateJson();
    }

    private boolean validateForm() {
        boolean valid = true;

        if (getInt(etId) <= 0) {
            etId.setError("事件ID必须大于0");
            valid = false;
        }
        if (getInt(etConquerId) <= 0) {
            etConquerId.setError("征服ID必须大于0");
            valid = false;
        }
        if (getInt(etEventBuffId1) <= 0) {
            etEventBuffId1.setError("BUFF1 ID必须大于0");
            valid = false;
        }
        if (getInt(etRound1) <= 0) {
            etRound1.setError("持续回合必须大于0");
            valid = false;
        }
        int chance = getInt(etChance);
        if (chance < 0 || chance > 100) {
            etChance.setError("触发概率必须在0-100之间");
            valid = false;
        }
        return valid;
    }

    private void resetForm() {
        etId.setText("101");
        etConquerId.setText("1");
        etEventBuffId1.setText("201");
        etRound1.setText("5");
        etCountryId1.setText("1,2");
        etEventBuffId2.setText("0");
        etRound2.setText("0");
        etCountryId2.setText("");
        etTrigger.setText("1");
        etTriggerValueStr.setText("");
        etLocation.setText("");
        etChance.setText("100");

        updateTriggerInfo();
        generateJson();
        showToast("表单已重置");
    }

    // ============ Trigger Info ============

    private void updateTriggerInfo() {
        int trigger = getInt(etTrigger);
        String desc = Event.getTriggerDescription(trigger, parseIntList(etTriggerValueStr.getText().toString()));
        tvTriggerInfo.setText(desc);
    }

    // ============ Templates ============

    private Event createRoundTemplate() {
        Event event = new Event();
        event.setId(101);
        event.setConquerId(1);
        event.setEventBuffId1(201);
        event.setRound1(5);
        List<Integer> c1 = new ArrayList<>();
        c1.add(1); c1.add(2);
        event.setCountryId1(c1);
        event.setEventBuffId2(0);
        event.setRound2(0);
        event.setCountryId2(new ArrayList<Integer>());
        event.setTrigger(1);
        List<Integer> tv = new ArrayList<>();
        tv.add(3); tv.add(10);
        event.setTriggerValue(tv);
        List<Integer> loc = new ArrayList<>();
        loc.add(15); loc.add(16);
        event.setLocation(loc);
        event.setChance(50);
        return event;
    }

    private Event createConquerTemplate() {
        Event event = new Event();
        event.setId(102);
        event.setConquerId(2);
        event.setEventBuffId1(202);
        event.setRound1(3);
        List<Integer> c1 = new ArrayList<>();
        c1.add(3);
        event.setCountryId1(c1);
        event.setEventBuffId2(0);
        event.setRound2(0);
        event.setCountryId2(new ArrayList<Integer>());
        event.setTrigger(2);
        List<Integer> tv = new ArrayList<>();
        tv.add(1);
        event.setTriggerValue(tv);
        List<Integer> loc = new ArrayList<>();
        loc.add(25);
        event.setLocation(loc);
        event.setChance(100);
        return event;
    }

    private Event createAttackTemplate() {
        Event event = new Event();
        event.setId(103);
        event.setConquerId(3);
        event.setEventBuffId1(203);
        event.setRound1(4);
        List<Integer> c1 = new ArrayList<>();
        c1.add(4); c1.add(5);
        event.setCountryId1(c1);
        event.setEventBuffId2(204);
        event.setRound2(2);
        List<Integer> c2 = new ArrayList<>();
        c2.add(6);
        event.setCountryId2(c2);
        event.setTrigger(3);
        List<Integer> tv = new ArrayList<>();
        tv.add(2);
        event.setTriggerValue(tv);
        List<Integer> loc = new ArrayList<>();
        loc.add(30); loc.add(31);
        event.setLocation(loc);
        event.setChance(75);
        return event;
    }

    private Event createGeneralTemplate() {
        Event event = new Event();
        event.setId(104);
        event.setConquerId(4);
        event.setEventBuffId1(205);
        event.setRound1(6);
        List<Integer> c1 = new ArrayList<>();
        c1.add(7);
        event.setCountryId1(c1);
        event.setEventBuffId2(0);
        event.setRound2(0);
        event.setCountryId2(new ArrayList<Integer>());
        event.setTrigger(6);
        List<Integer> tv = new ArrayList<>();
        tv.add(101);
        event.setTriggerValue(tv);
        List<Integer> loc = new ArrayList<>();
        loc.add(40);
        event.setLocation(loc);
        event.setChance(100);
        return event;
    }

    private void loadTemplate(Event template) {
        populateForm(template);
        showToast("模板已加载");
    }

    private void filterTemplates() {
        String search = etTemplateSearch.getText().toString().toLowerCase().trim();
        // For now just show toast, since templates are buttons not list items
        if (!search.isEmpty()) {
            boolean found = false;
            if (search.contains("回合") || search.contains("round")) found = true;
            if (search.contains("占领") || search.contains("conquer")) found = true;
            if (search.contains("攻击") || search.contains("attack")) found = true;
            if (search.contains("击败") || search.contains("general")) found = true;
            if (found) showToast("可用模板已在按钮中显示");
        }
    }

    private Event createDefaultEvent() {
        return createRoundTemplate();
    }

    // ============ JSON Operations ============

    private void generateJson() {
        try {
            Event event = collectForm();
            String json = gson.toJson(event);
            tvOutput.setText(json);
        } catch (Exception e) {
            tvOutput.setText("// 生成失败: " + e.getMessage());
        }
    }

    private void copyToClipboard() {
        String text = tvOutput.getText().toString();
        if (text.startsWith("//")) {
            showToast("无数据可复制");
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("JSON", text);
        clipboard.setPrimaryClip(clip);
        showToast("已复制到剪贴板");
    }

    private void formatJson() {
        String text = tvOutput.getText().toString();
        String formatted = JsonHelper.formatJson(text);
        tvOutput.setText(formatted);
        showToast("JSON已格式化");
    }

    private void minifyJson() {
        String text = tvOutput.getText().toString();
        String minified = JsonHelper.minifyJson(text);
        tvOutput.setText(minified);
        showToast("JSON已压缩");
    }

    private void exportJson() {
        String text = tvOutput.getText().toString();
        if (text.startsWith("//")) {
            showToast("没有可导出的JSON数据");
            return;
        }
        try {
            String json;
            if (eventData.isEmpty()) {
                // Export current single event
                json = text;
            } else {
                // Export the entire event list
                json = JsonHelper.toPrettyJson(eventData);
            }

            String savedPath = JsonHelper.saveToCache(this, json, "ConquerEventSettings.json");
            if (savedPath != null) {
                showToast("已保存到: " + savedPath);
            } else {
                // Fallback: write directly
                File cacheDir = getCacheDir();
                File file = new File(cacheDir, "ConquerEventSettings.json");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(json.getBytes());
                fos.close();
                showToast("已导出到: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showToast("导出失败: " + e.getMessage());
        }
    }

    // ============ Helpers ============

    private int getInt(EditText et) {
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private List<Integer> parseIntList(String text) {
        List<Integer> result = new ArrayList<>();
        if (TextUtils.isEmpty(text)) return result;
        String[] parts = text.split(",");
        for (String part : parts) {
            try {
                int val = Integer.parseInt(part.trim());
                result.add(val);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private String formatIntList(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";
        return TextUtils.join(",", list);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
