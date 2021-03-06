package couldmsu.project.nimbusio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by VishnuChaitanya on 11/12/2016.
 * Packages: java.net; java.IO
 * Description: Class works as background activity to Sign Up Button pushed on LoginActivity class.
 * Gets the first name, last name, username, password, email address, phone number from the app and
 * connects to the php url. Send all the user information to server through the link. PHP writes to the Database.
 * gets the response that is echoed on php.
 * Uses the result to decide on what needs to be done next.
 */

public class BgWorkerSignUp extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String un;
    BgWorkerSignUp (Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String register_url = "http://104.236.47.53/register.php";

        if(type.equals("register")){
            try {
                String fn = params[1]; //First name
                String ln = params[2]; // last name
                un = params[3];         // Username
                String up = params[4];  // User password
                String ue = params[5];  // User email address
                String uph = params[6]; // User phone number
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("first_name","UTF-8")+"="+URLEncoder.encode(fn,"UTF-8")+"&"
                        + URLEncoder.encode("last_name","UTF-8")+"="+URLEncoder.encode(ln,"UTF-8")+"&"
                        + URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(un,"UTF-8")+"&"
                        + URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(up,"UTF-8")+"&"
                        + URLEncoder.encode("user_email","UTF-8")+"="+URLEncoder.encode(ue,"UTF-8")+"&"
                        + URLEncoder.encode("user_phone","UTF-8")+"="+URLEncoder.encode(uph,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Sign up status");
    }

    @Override
    protected void onPostExecute(String result) {
        alertDialog.setMessage(result.toString());
        //result from php is being used for deciding on what to do next

        if(result.equals("Not a valid email")){

            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
        }
        else if (result.equals("username or email exists")){

            Toast.makeText(context, "Enter a different username and email address.", Toast.LENGTH_LONG).show();
        }
        else if(result.equals("Account Created")){
            Toast.makeText(context, "Account created!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(context, Home.class);
            i.putExtra("unfromlogin",un);
            context.startActivity(i);
        }
        else{
            alertDialog.show();
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}