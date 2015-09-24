package mc.assign1.rockpaperscissor;

import java.util.Random;



import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnTouchListener {

	private static final int ROCK = 0;
	private static final int PAPER = 1;
	private static final int SCISSOR = 2;
	private static String WIN = "win";
	private static String LOSS = "loss";
	private static String DRAW = "draw";
	private String mConnectedDeviceName;
	private String userName,mode,deviceaddress;
	TextView Result;
	DBDelegate dbDelegate;
	private boolean flag = false;
	TextView win;
	TextView loss;
	TextView draw;
	TextView user, opponent;
	ImageView ivopp;
	BluetoothAdapter btAdp = BluetoothAdapter.getDefaultAdapter();
	private BluetoothGameService mGameService = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		dbDelegate = new DBDelegate(getApplicationContext());

		win = (TextView) findViewById(R.id.textView5);
		loss = (TextView) findViewById(R.id.textView6);
		draw = (TextView) findViewById(R.id.textView7);
		user = (TextView) findViewById(R.id.textView11);
		opponent = (TextView) findViewById(R.id.textView12);
		Result = (TextView) findViewById(R.id.textView13);
		userName = this.getIntent().getExtras().getString("userName");
		mode = this.getIntent().getExtras().getString("mode");
		if(mode.equalsIgnoreCase(SelectActivity.MULTIPLAYER)){
		deviceaddress = this.getIntent().getExtras().getString("deviceaddress");
		BluetoothDevice device = btAdp.getRemoteDevice(deviceaddress);
        System.out.println("Game activity: device: "+device);
		mGameService  = new BluetoothGameService(getApplicationContext(), mHandler);
        mGameService.connect(device);
        
		}
		System.out.println("Game Activity: device: "+deviceaddress);
		
		final ImageView iv = (ImageView) findViewById(R.id.imageView1);
		ivopp = (ImageView) findViewById(R.id.imageView2);
		Context cnt = null;
		dbDelegate.getStats(userName, win, loss, draw);
		iv.setOnTouchListener(new OnSwipeTouchListener(cnt) {

			public void onSwipeRight() {
				iv.setImageResource(R.drawable.paper);
				getGameResult(PAPER);
				dbDelegate.getStats(userName, win, loss, draw);
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						iv.setImageResource(R.drawable.abc_ab_solid_dark_holo);
						ivopp.setImageResource(R.drawable.opponent);
					}
				};
				iv.postDelayed(runnable, 2000);
			}
			public void onSwipeLeft() {
				iv.setImageResource(R.drawable.rock);
				getGameResult(ROCK);
				dbDelegate.getStats(userName, win, loss, draw);
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						iv.setImageResource(R.drawable.abc_ab_solid_dark_holo);
						ivopp.setImageResource(R.drawable.opponent);
					}
				};
				iv.postDelayed(runnable, 2000);
			}
			public void onSwipeBottom() {
				iv.setImageResource(R.drawable.scissors);
				getGameResult(SCISSOR);
				dbDelegate.getStats(userName, win, loss, draw);
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						iv.setImageResource(R.drawable.abc_ab_solid_dark_holo);
						ivopp.setImageResource(R.drawable.opponent);
					}
				};
				iv.postDelayed(runnable, 2000);
			}

			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// System.out.println(event.getPointerCount());
		return super.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								GameActivity.this.finish();
							}
						}).setNegativeButton("No", null).show();
	}

	public String getGameResult(int input) {
		if(mode.equalsIgnoreCase(SelectActivity.SINGLEPLAYER)){
		int systemChoice;
		Random randomGenerator = new Random();
		systemChoice = randomGenerator.nextInt(3);
		String result = null;
		switch (input) {
			case ROCK :
				if (systemChoice == PAPER)
					result = LOSS;
				else if (systemChoice == SCISSOR)
					result = WIN;
				else
					result = DRAW;
				break;
			case PAPER :
				if (systemChoice == SCISSOR)
					result = LOSS;
				else if (systemChoice == ROCK)
					result = WIN;
				else
					result = DRAW;
				break;
			case SCISSOR :
				if (systemChoice == ROCK) {
					result = LOSS;
				} else if (systemChoice == PAPER) {
					result = WIN;
				} else {
					result = DRAW;
				}
				break;
		}
		if (result != null) {
			displayTextboxes(input, systemChoice, result);
			dbDelegate.updateDB(this.userName, result);
		}
		
		return result;
		}
		else if(mode.equalsIgnoreCase(SelectActivity.MULTIPLAYER)){
			switch (input) {
			case ROCK :
				flag = true;
				sendMessage("Rock");
				
				/*if (systemChoice == PAPER)
					result = LOSS;
				else if (systemChoice == SCISSOR)
					result = WIN;
				else
					result = DRAW;*/
				break;
			case PAPER :
				flag = true;
				sendMessage("Paper");
				
				/*if (systemChoice == SCISSOR)
					result = LOSS;
				else if (systemChoice == ROCK)
					result = WIN;
				else
					result = DRAW;
*/				break;
			case SCISSOR :
				flag = true;
				sendMessage("Scissors");
				
				/*if (systemChoice == ROCK) {
					result = LOSS;
				} else if (systemChoice == PAPER) {
					result = WIN;
				} else {
					result = DRAW;
				}*/
				break;
		}
		/*if (result != null) {
			displayTextboxes(input, systemChoice, result);
			dbDelegate.updateDB(this.userName, result);
		}*/
			
			return "";
		}
		return "";
	}
	private void displayTextboxes(int userinput, int oppinput, String result) {
		// TODO Auto-generated method stub
		System.out.println("User: " + userinput + " oppinput: " + oppinput
				+ " Resu: " + result);
		Result.setText(result);
		if (userinput == 0) {
			user.setText("Rock");
		} else if (userinput == 1) {
			user.setText("Paper");
		} else {
			user.setText("Scissors");
		}
		if (oppinput == 0) {
			opponent.setText("Rock");
			ivopp.setImageResource(R.drawable.rock);
		} else if (oppinput == 1) {
			opponent.setText("Paper");
			ivopp.setImageResource(R.drawable.paper);
		} else {
			opponent.setText("Scissors");
			ivopp.setImageResource(R.drawable.scissors);
		}
		if (result == WIN) {
			Result.setTextColor(Color.GREEN);
		} else if (result == LOSS) {
			Result.setTextColor(Color.RED);
		} else {
			Result.setTextColor(Color.BLUE);
		}

	}
	 private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case BluetoothActivity.MESSAGE_STATE_CHANGE:
	                switch (msg.arg1) {
	                case BluetoothGameService.STATE_CONNECTED:
	                    break;
	                case BluetoothGameService.STATE_CONNECTING:
	                    break;
	                case BluetoothGameService.STATE_LISTEN:
	                	break;
	                case BluetoothGameService.STATE_NONE:
	                    break;
	                }
	                break;
	            case BluetoothActivity.MESSAGE_WRITE:
	            	byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                String writeMessage = new String(writeBuf);
	                Toast.makeText(getApplicationContext(), "You played "
	                        + writeMessage, Toast.LENGTH_SHORT).show();
	                break;
	            case BluetoothActivity.MESSAGE_READ:
	            	while(!flag) {
	            		
	            	}
	            	
	            	byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                String readMessage = new String(readBuf, 0, msg.arg1);
	            	Toast.makeText(getApplicationContext(), "Opponent played "
	                        + readMessage, Toast.LENGTH_SHORT).show();
	                break;
	            case BluetoothActivity.MESSAGE_DEVICE_NAME:
	                // save the connected device's name
	                mConnectedDeviceName = msg.getData().getString(BluetoothActivity.DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to "
	                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                break;
	            case BluetoothActivity.MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothActivity.TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
		 
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mGameService.getState() != BluetoothGameService.STATE_CONNECTED) {
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
        	flag =true;
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mGameService.write(send);
        }
    }
}
