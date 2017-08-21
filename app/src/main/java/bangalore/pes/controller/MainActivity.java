package bangalore.pes.controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.CursorIndexOutOfBoundsException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Main Activity"; //TAG for the class

    // Buttons
    Button On,Edit;

    DatabaseHelper myDB; //object to access inbuilt sqlite database.

    Spinner spinner; // drop down list to display all stored materials.

    List<Materials>materialsList = new ArrayList<>(); // list to hold all objects of type Materials class.
    List<String>materials_names = new ArrayList<>(); // list to hold all material names.

    //Bluetooth related variables
    private BluetoothAdapter mBluetoothAdapter; // for establishing Bluetooth in the android device
    private BluetoothDevice arduinoDevice; // HC-05 module

    String address; // to hold address of selected device received from previous activity.
    String name; // to hold name of selected device received from previous activity.

    String material, p1, p2; // variables which store selected material name along with its parameter values.
    String toolingFactor; // to store the entered tooling factor
    EditText editText4,editText2,editText3; // to enter data

    RelativeLayout relativeLayout; // object used when onConfigurationChanged override function is called.


    // The app crashes when the device is rotated. This override function takes care of saving the activity state when the configuration is changed (screen rotation)
    // This override function basically restarts the entire activity, thus preventing the crashing.
    // Basically, whatever has been initialized in the onCreate function has to be initialized again, which looks like it is saving the activity state.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

        // The state of the layout of activity has to be saved.
        relativeLayout = (RelativeLayout)findViewById(R.id.MainRelativeLayout);
        relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams dimensions = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(dimensions);


        Intent newint = getIntent();
        address = newint.getStringExtra(ListAvailableDevices.EXTRA_ADDRESS);
        name = newint.getStringExtra(ListAvailableDevices.EXTRA_NAME);


        On=(Button)findViewById(R.id.button);
        Edit=(Button)findViewById(R.id.button3);

        myDB=new DatabaseHelper(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        spinner=(Spinner)findViewById(R.id.spinner);


        try{
            materialsList=myDB.getAllMaterials();}catch (CursorIndexOutOfBoundsException e)
        {
            myDB.insert(new Materials("Aluminium","40.5","67.4"));
            materialsList = myDB.getAllMaterials();
        }

        if(materialsList.size()==0)
        {
            myDB.insert(new Materials("Aluminium","34.3","55.7"));
        }


        for(Materials material : materialsList)
        {
            materials_names.add(material.getMaterial());
        }
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,materials_names);
        spinner.setAdapter(arrayAdapter);

        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText2.setText(p1);
        editText3.setText(p2);
        editText4.setText(toolingFactor);

        On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DisplayActivityIntent=new Intent(MainActivity.this,DisplayActivity.class);
                DisplayActivityIntent.putExtra("p1",p1);
                DisplayActivityIntent.putExtra("p2",p2);
                DisplayActivityIntent.putExtra("toolingFactor",p1);
                DisplayActivityIntent.putExtra("name",name);
                DisplayActivityIntent.putExtra("address",address);
                startActivity(DisplayActivityIntent);
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent EditActivity=new Intent(MainActivity.this,Edit.class);
                myDB.closeDB();
                startActivity(EditActivity);
            }
        });


        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                p1 = editable.toString();



            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                p2 = editable.toString();


            }
        });

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                toolingFactor = editable.toString();


            }
        });




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization of the layout of the activity.
        relativeLayout = (RelativeLayout)findViewById(R.id.MainRelativeLayout);
        relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams dimensions = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(dimensions);
//        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        // Intent to receive the selected device name and address from the previous activity.
        Intent newint = getIntent();
        address = newint.getStringExtra(ListAvailableDevices.EXTRA_ADDRESS);
        name = newint.getStringExtra(ListAvailableDevices.EXTRA_NAME);


        // Initialization of button
        On=(Button)findViewById(R.id.button);
        Edit=(Button)findViewById(R.id.button3);
        myDB=new DatabaseHelper(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // initializing BluetoothAdapter

        // Initializing the edit text fields.
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);

        // Initializing spinner.
        spinner=(Spinner)findViewById(R.id.spinner);


        try{
        materialsList=myDB.getAllMaterials(); // stores all the Materials objects.
        }
        catch (CursorIndexOutOfBoundsException e) // this exception occurs when the database is empty (happens when the app is just installed)
        { // So, manually add a dummy material to handle this error
            myDB.insert(new Materials("Aluminium","40.5","67.4"));
            materialsList = myDB.getAllMaterials();
        }

        for(Materials material : materialsList)
        {
            materials_names.add(material.getMaterial()); // stores all material names in the list
        }
        spinner.setOnItemSelectedListener(this); // make items in the drop down list clickable

        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,materials_names); // arrayAdapter object initialized.
        spinner.setAdapter(arrayAdapter); // Sets the object to the spinner object. Now, the  drop down list will be displayed in the activity.

        // detects change in the edit field.
        // When the material is selected, the parameter values are displayed in the editText fields. If the values is changed on spot, the editText does not detect this change unless this method is invoked.
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                p1 = editable.toString();



            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                p2 = editable.toString();


            }
        });

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                toolingFactor = editable.toString();


            }
        });
        // Clicking ON button,
        On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DisplayActivityIntent=new Intent(MainActivity.this,DisplayActivity.class); // Intent to go to DisplayActivity class.
                // Takes all the selected material values, tooling factor and the device name and address to the DisplayActivity.
                DisplayActivityIntent.putExtra("p1",p1);
                DisplayActivityIntent.putExtra("p2",p2);
                DisplayActivityIntent.putExtra("toolingFactor",p1);
                DisplayActivityIntent.putExtra("name",name);
                DisplayActivityIntent.putExtra("address",address);

                startActivity(DisplayActivityIntent);
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent EditActivity=new Intent(MainActivity.this,Edit.class); // Intent to go to Edit class, when CRUD operations can be done.
                myDB.closeDB();
                startActivity(EditActivity);
            }
        });
    }

    // It's better to close database when the activity is destroyed.
    @Override
    protected void onDestroy() {
        myDB.closeDB();
        super.onDestroy();

    }

    // Override function is called when an item is clicked from the drop-down list.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        material = adapterView.getItemAtPosition(i).toString(); // to get name of the selected list item

        for(String name_material : materials_names)
        {
            if(name_material.equals(material))
            {
                for(Materials material : materialsList)
                {
                    if(name_material.equals(material.getMaterial()))
                    {
                        // to display the parameter values of the selected material in the edit text fields.
                        editText2.setText(material.getParameter_1());
                        editText3.setText(material.getParameter_2());

                    }
                }
            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // called from the onResume function
    // Updates the spinner if any changes have been made in the database.
    public void updateSpinner()
    {
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,materials_names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);




    }
    @Override
    public void onResume()
    {
        super.onResume();
        updateSpinner();

    }




}