package wifi.datatransfer;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SimpleAsynTask {

    WifiSocket ws = new WifiSocket(this);
    private Context context;

    public SimpleAsynTask(Context context){
        this.context=context;
    }
	 public void runAsynTask(Runnable task){
   	  
     	  new simpleTask(task).execute();
     	  
     	  }
     class simpleTask extends AsyncTask<Object,Void,String>
     {
       
         private Runnable mTask;

         protected simpleTask(Runnable task)
         {
             super();
             mTask = task;
            
         }

         protected void onPreExecute()
         {
       	      	 Log.i("Receive file :: ", "Starting... ");
         }

         protected void onPostExecute(String success)
         {
             if (success == null)
             {
           	   	 Log.i("Receive file :: ", "succeed");
//                 Intent intent = new Intent(context, TestFileSend.class);
//                 context.startActivity(intent);
//                 ((Activity)context).finish();
             }
             else
             {
           	 
            	 Log.e("Receive file :: ", "Failed with exception '" + success);
            }
         }

         protected String doInBackground(final Object... args)
         {
         	
             String success = null;
             try 
             {
            	 mTask.run();
                
             }
             catch (Exception e)
             {
               success = e.toString();
             }

             return success;
         }
     }   
    
}
