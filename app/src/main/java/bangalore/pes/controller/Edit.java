package bangalore.pes.controller;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.app.Activity;

import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Edit extends Activity implements AdapterView.OnItemSelectedListener {
    DatabaseHelper myDB;// object to access sqlite database.

    EditText editText1,editText2,editText3; // editText fields to add or update data

    Button Add,Update,Delete,Submit,View,Back; // Buttons

    String material,p1,p2;

    Spinner spinner; // to display the list of already existing materials in the database

    // boolean variables used to differentiate different operations on clicking SUBMIT button
    boolean clickedUpdate = false;
    boolean clickedDelete = false;
    boolean clickedAdd = false;

    List<String>send_materials = new ArrayList<>(); //
    List<Materials> materialsList = new ArrayList<>(); // list to store Materials objects
    List<String>materials_names = new ArrayList<>(); // list to store material names.

    String UpdateMaterial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        myDB=new DatabaseHelper(this); // initializing database object
        // initializing button objects
        Add=(Button)findViewById(R.id.button4);
        View=(Button)findViewById(R.id.button13);
        Update=(Button)findViewById(R.id.button10);
        Delete=(Button)findViewById(R.id.button11);
        Submit=(Button)findViewById(R.id.button12);
        Back=(Button)findViewById(R.id.button14);

        spinner=(Spinner)findViewById(R.id.spinner3); //initializing spinner object

        // Making submit button invisible.
        Submit.setVisibility(View.INVISIBLE);
        Submit.setEnabled(false);

        // initializing editText objects
        editText1=(EditText)findViewById(R.id.editText5);
        editText2=(EditText)findViewById(R.id.editText6);
        editText3=(EditText)findViewById(R.id.editText7);

        // Making all editText fields invisible and disabling them.
        editText1.setEnabled(false);
        editText1.setVisibility(View.INVISIBLE);
        editText2.setEnabled(false);
        editText2.setVisibility(View.INVISIBLE);
        editText3.setEnabled(false);
        editText3.setVisibility(View.INVISIBLE);

        materialsList=myDB.getAllMaterials(); // gets all materials from the database and stores it in the list

        materials_names.clear();

        for(Materials material : materialsList)
        {
            materials_names.add(material.getMaterial()); // adds all material names to the list
        }

        spinner.setOnItemSelectedListener(this); // makes all spinner items clickable

        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,materials_names);
        spinner.setAdapter(arrayAdapter);

        spinner.setVisibility(View.INVISIBLE); // makes the spinner invisible

        Submit.setEnabled(false);
        // On clicking ADD,
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disables Add, Update,Delete,and View buttons and makes Submit Button Visible
                Update.setEnabled(true);
                Submit.setVisibility(View.VISIBLE);
                Submit.setEnabled(true);
                Update.setEnabled(false);
                View.setEnabled(false);
                Delete.setEnabled(false);

                clickedAdd = true;
                clickedDelete = false;
                clickedUpdate = false;
                // enables and makes all editText fields visible.
                editText1.setEnabled(true);
                editText1.setVisibility(View.VISIBLE);
                editText2.setEnabled(true);
                editText2.setVisibility(View.VISIBLE);
                editText3.setEnabled(true);
                editText3.setVisibility(View.VISIBLE);
                Add.setEnabled(false);


            }
        });
        // On clicking UPDATE,
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disables Update,Add,Delete,and View buttons and makes Submit Button and spinner Visible
                Add.setEnabled(false);
                Update.setEnabled(false);
                View.setEnabled(false);
                Delete.setEnabled(false);
                clickedDelete =false;
                clickedUpdate = true;
                clickedAdd = false;
                spinner.setVisibility(View.VISIBLE);
                Submit.setVisibility(View.VISIBLE);
                Submit.setEnabled(true);

                Toast.makeText(getApplicationContext(),"Please enter select material name",Toast.LENGTH_SHORT).show();
                // enables and makes all editText fields visible.
                editText2.setEnabled(true);
                editText2.setVisibility(View.VISIBLE);
                editText3.setEnabled(true);
                editText3.setVisibility(View.VISIBLE);

                // This feature considers new values for calculation.
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

                Update.setEnabled(false);


            }
        });

        // On clicking DELETE,
        Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickedDelete = true;
                clickedUpdate = false;
                clickedAdd = false;
                Add.setEnabled(false);
                Update.setEnabled(false);
                View.setEnabled(false);
                Delete.setEnabled(false);
                Submit.setVisibility(View.VISIBLE);
                Submit.setEnabled(true);
                spinner.setVisibility(View.VISIBLE);
                editText1.setEnabled(false);
                editText1.setVisibility(View.INVISIBLE);
                editText2.setEnabled(false);
                editText2.setVisibility(View.INVISIBLE);
                editText3.setEnabled(false);
                editText3.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(),"Select the element to be deleted",Toast.LENGTH_SHORT).show();



            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedUpdate == true) // becomes true when Update Button is clicked
                {
                    // Pop up message
                    final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Edit.this,R.style.MyDialogTheme);
                    alertDialogBuilder.setMessage("Are you sure, to make these changes?");

                    //On clicking Yes,
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Materials materials=new Materials(UpdateMaterial,p1,p2); // creates an object with new values.
                                    myDB.updateMaterials(materials); // updates the values
                                    Toast.makeText(getApplicationContext(),"Values updated",Toast.LENGTH_SHORT).show();
                                    updateSpinner(); // updates spinner with changes
                                    // All Buttons are enables.
                                    Add.setEnabled(true);
                                    Update.setEnabled(true);
                                    Delete.setEnabled(true);
                                    View.setEnabled(true);



                                }
                            });

                    // On clicking No,
                    alertDialogBuilder.setNegativeButton("no",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            });
                    alertDialogBuilder.show();
                    Update.setEnabled(true);




                }
                else if(clickedDelete == true) // Becomes true when Delete button is clicked.
                {
                    final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Edit.this);
                    alertDialogBuilder.setMessage("Are you sure, to delete this element?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    myDB.deleteMaterials(material); // material is deleted from the database along woth its values.
                                    Toast.makeText(getApplicationContext(),"Element deleted",Toast.LENGTH_SHORT).show();
                                    updateSpinner(); // updates the spinner with changes.
                                    Add.setEnabled(true);
                                    Update.setEnabled(true);
                                    View.setEnabled(true);


                                }
                            });
                    alertDialogBuilder.setNegativeButton("no",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            });
                    alertDialogBuilder.show();
                    Delete.setEnabled(true);


                }
                else if(clickedAdd == true) // becomes true on clicking Add button
                {
                    final String material_1=editText1.getText().toString();
                    p1=editText2.getText().toString();
                    p2=editText3.getText().toString();
                    final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Edit.this);
                    alertDialogBuilder.setMessage("Confirm?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Materials material_obj=new Materials(material_1,p1,p2); // creates a new object for a new material along with its parameter values

                                    myDB.insert(material_obj); // new material added to the database

                                    Toast.makeText(getApplicationContext(),"Element added",Toast.LENGTH_SHORT).show();
                                    updateSpinner(); // updates spinner with changes
                                    // Enables all buttons
                                    View.setEnabled(true);
                                    Update.setEnabled(true);
                                    Delete.setEnabled(true);



                                }
                            });
                    alertDialogBuilder.setNegativeButton("no",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            });
                    alertDialogBuilder.show();


                    Add.setEnabled(true);


                }

            }
        });

        // To view all materials in the database
        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                Intent ViewActivity = new Intent(Edit.this,bangalore.pes.controller.View.class);
                startActivity(ViewActivity);}catch (ActivityNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        });
        // To go back to the previous activity.
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainActivityIntent=new Intent(Edit.this,MainActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("MATERIALSLIST",(Serializable)send_materials);
                MainActivityIntent.putExtra("BUNDLE",args);
                startActivityForResult(MainActivityIntent,0);
            }
        });


    }
    @Override
    protected void onDestroy() {
        myDB.closeDB();
        super.onDestroy();

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(clickedUpdate == true) // becomes true when Update button is clicked.
        {
            material=adapterView.getItemAtPosition(i).toString(); // gets the selected material name
            UpdateMaterial = material;
            editText1.setEnabled(true);
            editText1.setVisibility(View.VISIBLE);
            editText1.setText(material);
            editText2.setEnabled(true);
            editText2.setVisibility(View.VISIBLE);
            editText3.setEnabled(true);
            editText3.setVisibility(View.VISIBLE);
            p1=editText2.getText().toString();
            p2=(editText3).getText().toString();

        }
        if(clickedDelete = true)
        {
            material=adapterView.getItemAtPosition(i).toString();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // function to update spinner after CRUD operations are done
    public void updateSpinner()
    {

        materialsList=myDB.getAllMaterials();
        materials_names.clear();
        for(Materials material : materialsList)
        {
            materials_names.add(material.getMaterial());
        }
        for(String materials : materials_names)
        {
            send_materials.add(materials);
        }

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,materials_names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);




    }
}
