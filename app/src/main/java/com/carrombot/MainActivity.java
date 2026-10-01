package com.carrombot;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private TextView statusText;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText=findViewById(R.id.statusText);
        Button btn=findViewById(R.id.enableBtn);
        btn.setOnClickListener(v->{
            if(!isEnabled()){
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }else{
                statusText.setText("Bot চালু আছে ✓");
                CarromAccessibilityService.setBotEnabled(true);
            }
        });
    }
    @Override protected void onResume(){
        super.onResume();
        statusText.setText(isEnabled()?"Accessibility চালু — Bot শুরু করুন":"Accessibility Service বন্ধ");
    }
    private boolean isEnabled(){
        AccessibilityManager am=(AccessibilityManager)getSystemService(ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> list=am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for(AccessibilityServiceInfo i:list)if(i.getId().contains("com.carrombot"))return true;
        return false;
    }
}
