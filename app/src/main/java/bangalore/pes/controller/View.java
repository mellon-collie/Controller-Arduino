package bangalore.pes.controller;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class View extends AppCompatActivity {
    DatabaseHelper myDB=new DatabaseHelper(this);
    List<Materials>materialsList = new ArrayList<>();
    List<String>materials_names = new ArrayList<>();
    ListView listView;
    Button back;
    StringBuffer stringBuffer;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        listView = (ListView)findViewById(R.id.listView);
        back = (Button)findViewById(R.id.button8);
        materialsList.clear();
        materials_names.clear();
        materialsList= myDB.getAllMaterials();
        stringBuffer = new StringBuffer();
        for(Materials materials : materialsList)
        {

            stringBuffer.append("Material :"+ materials.getMaterial() + "\n ");
            stringBuffer.append("Parameter 1 :"+ materials.getParameter_1()+ "\n");
            stringBuffer.append("Parameter 2 :"+ materials.getParameter_2()+"\n");
            stringBuffer.append("\n");

        }

        materials_names.add(stringBuffer.toString());
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,R.layout.list_item,materials_names);
        listView.setAdapter(arrayAdapter);


    back.setOnClickListener(new android.view.View.OnClickListener() {
        @Override
        public void onClick(android.view.View view) {
            finish();
        }
    });

    }

    @Override
    protected void onDestroy() {
        myDB.closeDB();
        super.onDestroy();

    }
}
