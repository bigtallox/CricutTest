package com.brianmielke.cricuttext;

import android.os.AsyncTask;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// A class to perform HTTP requests.  Why use a library when you can implement it yourself!
public class HttpRequest extends AsyncTask<String, Void, String>
{
    private static final String TAG = "HTTP_TASK";

    final protected String endpoint;
    final protected String paramString;
    final protected String method;
    protected String responseStr;
    public Exception exception;
    protected String authorization;
    final protected HttpTaskHandler taskHandler;

    public interface HttpTaskHandler
    {
        void requestSuccessful(String jsonString);
        void requestFailed(Exception e);
    }

    public HttpRequest(String endpoint,
                       String paramString,
                       String method,
                       HttpTaskHandler taskHandler)
    {
        this.endpoint = endpoint;
        this.paramString = paramString;
        this.method = method;
        this.taskHandler = taskHandler;
        this.authorization =null;
    }

    public void setAuthorization( String authorization )
    {
        this.authorization = authorization;
    }

    protected String doInBackground(String... params)
    {
        URL url;

        responseStr = "";

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException
            {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException
            {
            }
        }};

        // Install the all-trusting trust manager
        SSLContext sc;
        try
        {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        HttpURLConnection conn;
        try
        {
            url=new URL(endpoint);

            conn=(HttpURLConnection)url.openConnection();

            conn.setRequestProperty("Content-type","application/json");
            conn.setRequestProperty("Accept","application/json");

            if ( authorization != null )
            {
                conn.setRequestProperty("Authorization", authorization);
            }

            if ( method.equals("POST") )
            {
                conn.setDoOutput(true);
            }
            else
            {
                if ( paramString != null && paramString.length()>0 )
                {
                    conn.setDoOutput(true);
                }
                else
                {
                    conn.setDoOutput(false);
                }
            }

            conn.setRequestMethod(method);

            conn.setConnectTimeout(5000); //set timeout to 5 seconds

            if ( paramString != null)
            {
                conn.setFixedLengthStreamingMode(paramString.getBytes().length);
            }


            if ( paramString != null)
            {
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(paramString);
                out.close();
            }

            Scanner inStream = new Scanner(conn.getInputStream());

            while(inStream.hasNextLine())
                responseStr+=(inStream.nextLine());

            inStream.close();
        }
        catch (Exception e)
        {
            this.exception = e;
        }

        return responseStr;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if ( this.exception == null )
        {
            if (result != null)
            {
                taskHandler.requestSuccessful(result);
            }
            else
            {
                taskHandler.requestFailed(new Exception());
            }
        }
        else
        {
            taskHandler.requestFailed(this.exception);
        }
    }

}