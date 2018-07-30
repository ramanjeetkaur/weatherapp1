package lambtoncollege.com.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;


// this is profile page where user fills his details and
// which is saved inside sqlite database and retrieved when come back

public class ProfileActivity extends AppCompatActivity {



    private static final int CONTENT_REQUEST=1337;
    private File output=null;
    ImageView profile;
    EditText fname,lname;
    SharedPreferences preferences;
    public static String PROFILE_PREFF = "profilepreff";
    String imageDecode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        profile = findViewById(R.id.profilePicture);
        fname = findViewById(R.id.firstName);
        lname = findViewById(R.id.lastName);
        preferences = getSharedPreferences(PROFILE_PREFF,MODE_PRIVATE);


        //when you click on the image to open the camera
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //this passes the intent to open open
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CONTENT_REQUEST);//this opens the camera window
                }
            }

        });
        fname.setText(preferences.getString("firstName","")); // setting the old data from shared preffs
        lname.setText(preferences.getString("lastName",""));///setting the old data from shared preffs
        if (preferences.getString("profilePic","").equals("")){


        }else {
            profile.setImageBitmap(decodeBase64(preferences.getString("profilePic",""))); //setting back the Image if data
            // is aleardy strored in sharedpreff
        }

        Button save = findViewById(R.id.save);
        //when you click on save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //in this 4 lines we are saving data into sharedprefferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("firstName",fname.getText().toString());
                editor.putString("lastName",lname.getText().toString());
                editor.putString("profilePic",imageDecode);
                editor.commit();
                //here below this we are call DbOperations class and saving data inside Sqlite Database
                DbOperations dop  = new DbOperations(ProfileActivity.this);
                int count =  dop.getCount(DbOperations.PROFILE_TABLE);
                if (count == 0){//database is empty then save data into it
                    dop.putProfile(dop,fname.getText().toString(),lname.getText().toString());
                }
                //after everthing done go to main activity
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();

            }
        });



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        finish();
    }

    //this method converts the bitmap image into a base64 string
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);// this is the converted string imageEncoded
        // which we will save into the database


        return imageEncoded;
    }


//the method below decodes back the encoded image to show onto the imageview
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override //this method is called when camera window closes and returns the image
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (requestCode == CONTENT_REQUEST && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data"); //here we get the image
                    profile.setImageBitmap(imageBitmap);// here we set that image to image view
                   imageDecode =   encodeTobase64(imageBitmap);

                }
            }
        }
    }
}
