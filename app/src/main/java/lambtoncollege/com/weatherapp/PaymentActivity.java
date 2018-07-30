package lambtoncollege.com.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//in this class payment details are saved in to Sqlite database and show back by retrieving it.
public class PaymentActivity extends AppCompatActivity {

    EditText cardNumber,expDate,cvv;
    Button saveBut;

    SharedPreferences preff;
    public  static String PAYMENT_PREFF = "payment_preffs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setTitle("Payment");
        cardNumber = findViewById(R.id.cardNo);
        expDate = findViewById(R.id.expDate);
        cvv = findViewById(R.id.cvv);
        preff = getSharedPreferences(PAYMENT_PREFF,MODE_PRIVATE);

        saveBut = findViewById(R.id.saveData);
        //when u click on save button it saves the data into database
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preff.edit();
                editor.putString("cardNumber",cardNumber.getText().toString());
                editor.putString("expDate",expDate.getText().toString());
                editor.putString("cvv",cvv.getText().toString());
                editor.commit();

                //here DbOperations class is called where
                DbOperations dop  = new DbOperations(PaymentActivity.this);
                int count =  dop.getCount(DbOperations.PAYMENT_TABLE);
                if (count == 0){
                    //here it puts all the information into database from the edit texts
                    dop.putPayment(dop,cardNumber.getText().toString(),cvv.getText().toString(),expDate.getText().toString());
                }
                cardNumber.setText(preff.getString("cardNumber",""));
                expDate.setText(preff.getString("expDate",""));
                cvv.setText(preff.getString("cvv",""));
                startActivity(new Intent(PaymentActivity.this,MainActivity.class));
                finish();


            }
        });

        cardNumber.setText(preff.getString("cardNumber",""));
        expDate.setText(preff.getString("expDate",""));
        cvv.setText(preff.getString("cvv",""));




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PaymentActivity.this,MainActivity.class));
        finish();
    }
}
