package cc.flydev.launcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cc.flydev.face.R;

public class Dialog extends Activity {
	private Button button1;
	private ImageView img1;
	private static final int LIGHT_NORMAL = 64;  
    private static final int LIGHT_50_PERCENT = 127;  
    private static final int LIGHT_75_PERCENT = 191;  
    private static final int LIGHT_100_PERCENT = 255;  
    private static final int LIGHT_AUTO = 0;  
    private static final int LIGHT_ERR = -1;  
    private PowerManager mPowerManager;  
	private ImageButton bt1;
	private ImageButton bt2;
	private ImageButton bt3;
	private ImageButton bt4;
	private ImageButton bt5;
	private ImageButton bt6;
	private WifiManager mWm;

	private Activity context;

	private AudioManager mAudioManager;
	private ImageView brightnessIv;
	 private ConnectivityManager mConnectivityManager; 
	  public static final String RINGER_MODE_CHANGED = "android.media.RINGER_MODE_CHANGED"; 
	  private static final String NETWORK_CHANGE = "android.intent.action.ANY_DATA_STATE";
	  private IntentFilter mIntentFilter;

	


	@Override
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_settings);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mWm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		 mPowerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		 mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 mIntentFilter = new IntentFilter();  
		 mIntentFilter.addAction("android.intent.action.ANY_DATA_STATE"); 
	        //��ӹ㲥���������˵Ĺ㲥  
	        mIntentFilter.addAction("android.media.RINGER_MODE_CHANGED");
		LayoutInflater inflater = LayoutInflater.from(this);
		 final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.background, null);
				
				final AlertDialog dialog = new AlertDialog.Builder(Dialog.this).create();
				dialog.setCancelable(false);
				dialog.show();
				WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
				lp.alpha=0.9f;
				dialog.getWindow().setAttributes(lp);
				dialog.getWindow().setContentView(layout);	
				Window window = dialog.getWindow();  
				//设置显示动画  
				window.setWindowAnimations(R.style.main_menu_animstyle);  
				 dialog.setOnKeyListener(new OnKeyListener() {
					
					@Override//������ؼ��˳�/
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK)
                        {
                            dialog.dismiss();
                            Dialog.this.finish();

                       
                            return true;
                        }
                        else
                        {
                        	
                        	return false;
                        }
						
					}
				});
				  ImageButton bt1=(ImageButton)layout.findViewById(R.id.dialog_wifi);

				 bt1.setOnClickListener(new OnClickListener() {
					//wifi
					@Override
					public void onClick(View v) {

						if (mWm.isWifiEnabled()) {
						mWm.setWifiEnabled(false);
						Toast.makeText(getBaseContext(), "wifi关闭", Toast.LENGTH_LONG).show();
						
						}
						else {
						mWm.setWifiEnabled(true);
						Toast.makeText(getBaseContext(), "wifi打开", Toast.LENGTH_LONG).show();
						
						}
						
					}
				});

				 bt1.setOnLongClickListener(new OnLongClickListener() {
				
					@Override
					public boolean onLongClick(View v) {
						   Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);  
			                  startActivity(intent);
						return false;
					}
				});
				 
				 ImageButton bt2=(ImageButton)layout.findViewById(R.id.dialog_more);
				bt2.setOnClickListener(new OnClickListener() {
					


					@SuppressWarnings("deprecation")
					@Override
					public void onClick(View v) {
						Intent intent =  new Intent(Settings.ACTION_SETTINGS);  
		                startActivity(intent);
						
					}
				});
				ImageButton bt3=(ImageButton)layout.findViewById(R.id.homesetting);
				bt3.setOnClickListener(new OnClickListener() {
					
					
					@Override
					public void onClick(View v) {
						
						Intent intent = new Intent();

						intent.setClassName("cc.flydev.face","cc.flydev.launcher.settings.SettingsActivity");
						startActivity(intent);
						
					}
				});
				ImageButton bt4=(ImageButton)layout.findViewById(R.id.dialog_sound);
				bt4.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						 Intent intent =  new Intent(Settings.ACTION_SOUND_SETTINGS);  
		                 startActivity(intent);
						return false;
					}
				});
				
				 bt4.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
				
						  setSilentMode();
						
					}
				});
				 
				final ImageButton bt5=(ImageButton)layout.findViewById(R.id.dialog_light);
				 bt5.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						Intent intent =  new Intent(Settings.ACTION_DISPLAY_SETTINGS);  
			            startActivity(intent);
						return false;
					}
				});
				 bt5.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						 setBrightStatus();  
					}
				});
				 ImageButton bt6=(ImageButton)layout.findViewById(R.id.dialog_network);
				 bt6.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					 
/*			           if (getMobileDataStatus())  
				           {  
				               setMobileDataStatus(false);  
				               Toast.makeText(getApplicationContext(), "�ƶ������ѹر�", Toast.LENGTH_SHORT).show();
				                
				           }  
				           else  
				           {  
				               setMobileDataStatus(true);  
				               Toast.makeText(getApplicationContext(), "�ƶ������ѿ���", Toast.LENGTH_SHORT).show();
				             
						
					}*/
					    Intent intent =  new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);  
				           startActivity(intent);
					}
				});

			

				 }
//			�÷�����ʱ�޷�ʵ������ر�
			  /*private boolean getMobileDataStatus()  
			    {  
			        String methodName = "getMobileDataEnabled";  
			        Class cmClass = mConnectivityManager.getClass();  
			        Boolean isOpen = null;  
			          
			        try   
			        {  
			            Method method = cmClass.getMethod(methodName, null);  
			  
			            isOpen = (Boolean) method.invoke(mConnectivityManager, null);  
			        }   
			        catch (Exception e)   
			        {  
			            e.printStackTrace();  
			        }  
			        return isOpen;  
			    }  
			  private void setMobileDataStatus(boolean enabled)   
			    {  
			        try   
			        {  
			            Class<?> conMgrClass = Class.forName(mConnectivityManager.getClass().getName());  
			            //�õ�ConnectivityManager��ĳ�Ա����mService��ConnectivityService���ͣ�  
			            Field iConMgrField = conMgrClass.getDeclaredField("mService");  
			            iConMgrField.setAccessible(true);  
			            //mService��Ա��ʼ��  
			            Object iConMgr = iConMgrField.get(mConnectivityManager);  
			            //�õ�mService��Ӧ��Class����  
			            Class<?> iConMgrClass = Class.forName(iConMgr.getClass().getName());  
			            �õ�mService��setMobileDataEnabled(�÷�����androidԴ���ConnectivityService����ʵ��)�� 
			             * �÷����Ĳ���Ϊ�����ͣ����Եڶ�������ΪBoolean.TYPE 
			               
			            Method setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(  
			                    "setMobileDataEnabled", Boolean.TYPE);  
			            setMobileDataEnabledMethod.setAccessible(true);  
//			            ����ConnectivityManager��setMobileDataEnabled���������������صģ��� 
//			             * ʵ���ϸ÷�����ʵ������ConnectivityService(ϵͳ����ʵ����)�е� 
			               
			            setMobileDataEnabledMethod.invoke(iConMgr, enabled);  
			        } catch (ClassNotFoundException e)   
			        {  
			            e.printStackTrace();  
			        } catch (NoSuchFieldException e)   
			        {  
			            e.printStackTrace();  
			        } catch (SecurityException e)   
			        {  
			            e.printStackTrace();  
			        } catch (NoSuchMethodException e)   
			        {  
			            e.printStackTrace();  
			        } catch (IllegalArgumentException e)   
			        {  
			            e.printStackTrace();  
			        } catch (IllegalAccessException e)   
			        {  
			            e.printStackTrace();  
			        } catch (InvocationTargetException e)   
			        {  
			            e.printStackTrace();  
			        }  
			    }  
*/
			  

			private int getBrightStatus()  
			    {  
			  
			       
			        int light = 0;  
			        boolean auto = false;  
			        ContentResolver cr = getContentResolver();  
			          
			        try   
			        {  
			            auto = Settings.System.getInt(cr,  
			                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;  
			            if (!auto)   
			            {  
			                light = Settings.System.getInt(cr,
			                        Settings.System.SCREEN_BRIGHTNESS, -1);  
			                if (light > 0 && light <= LIGHT_NORMAL)   
			                {  
			                    return LIGHT_NORMAL;  
			                }  
			                else if (light > LIGHT_NORMAL && light <= LIGHT_50_PERCENT)   
			                {  
			                    return LIGHT_50_PERCENT;  
			                }     
			                else if (light > LIGHT_75_PERCENT && light <= LIGHT_100_PERCENT)  
			                {  
			                    return LIGHT_100_PERCENT;  
			                }  
			            }   
			            else   
			            {  
			                return LIGHT_AUTO;  
			            }  
			        }   
			        catch (SettingNotFoundException e1)   
			        {  
			            // TODO Auto-generated catch block  
			            e1.printStackTrace();  
			        }  
			        return LIGHT_ERR;  
			      
			    }  
			 private void setBrightStatus()  
			    {  
			        int light = 0;  
			          
			        switch (getBrightStatus())  
			        {  
			        case LIGHT_NORMAL:  
			            light = LIGHT_50_PERCENT - 1;  
			            Toast.makeText(getApplicationContext(), "50%", Toast.LENGTH_SHORT).show();
			            break;  
			        case LIGHT_50_PERCENT:  
			            light = LIGHT_75_PERCENT - 1; 
			            Toast.makeText(getApplicationContext(), "75%", Toast.LENGTH_SHORT).show();

			            break;  
			        case LIGHT_75_PERCENT:  
			            light = LIGHT_100_PERCENT - 1;  
			            Toast.makeText(getApplicationContext(), "100%", Toast.LENGTH_SHORT).show();
			            break;  
			        case LIGHT_100_PERCENT:  
			            startAutoBrightness(getContentResolver());  
			            Toast.makeText(getApplicationContext(), "自动亮度关闭", Toast.LENGTH_SHORT).show();
			            break;  
			        case LIGHT_AUTO:  
			            light = LIGHT_NORMAL - 1;  
			            stopAutoBrightness(getContentResolver()); 
			            Toast.makeText(getApplicationContext(), "自动亮度开启", Toast.LENGTH_SHORT).show();;
			            break;  
			        case LIGHT_ERR:  
			            light = LIGHT_NORMAL - 1;  
			            break;  
			          
			        }  
			          
			        setLight(light);  
			        setScreenLightValue(getContentResolver(), light);  
			    }  

			private void setLight(int light) {
				 try  
			        {  
			            //�õ�PowerManager���Ӧ��Class����  
			            Class<?> pmClass = Class.forName(mPowerManager.getClass().getName());  
			            //�õ�PowerManager���еĳ�ԱmService��mServiceΪPowerManagerService���ͣ�  
			            Field field = pmClass.getDeclaredField("mService");  
			            field.setAccessible(true);  
			            //ʵ����mService  
			            Object iPM = field.get(mPowerManager);  
			            //�õ�PowerManagerService��Ӧ��Class����  
			            Class<?> iPMClass = Class.forName(iPM.getClass().getName());  
			            /*�õ�PowerManagerService�ĺ���setBacklightBrightness��Ӧ��Method���� 
			             * PowerManager�ĺ���setBacklightBrightnessʵ����PowerManagerService�� 
			             */  
			            Method method = iPMClass.getDeclaredMethod("setBacklightBrightness", int.class);  
			            method.setAccessible(true);  
			            //����ʵ��PowerManagerService��setBacklightBrightness  
			            method.invoke(iPM, light);  
			        }  
			        catch (ClassNotFoundException e)  
			        {  
			            // TODO Auto-generated catch block  
			            e.printStackTrace();  
			        }  
			        catch (NoSuchFieldException e)  
			        {  
			            // TODO Auto-generated catch block  
			            e.printStackTrace();  
			        }  
			        catch (IllegalArgumentException e)  
			        {  
			            // TODO Auto-generated catch block  
			            e.printStackTrace();  
			        }  
			        catch (IllegalAccessException e)  
			        {  
			            // TODO Auto-generated catch block  
			            e.printStackTrace();  
			        }  
			        catch (NoSuchMethodException e)  
			        {  
			            // TODO Auto-generated catch block  
			            e.printStackTrace();  
			        }  
			        catch (InvocationTargetException e)  
			        {  
			            // TODO Auto-generated catch block  
			            e.printStackTrace();  
			        }  
			  
				
			}

			private void setScreenLightValue(ContentResolver resolver,
					int value) {
				 Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
			                value);
				
			}

			private void stopAutoBrightness(ContentResolver cr) {
				Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,  
		                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  
				
			}

			private void startAutoBrightness(ContentResolver cr) {
				Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,  
		                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC); 
				
			}

			private int getSilentStatus() {
				// TODO Auto-generated method stub
				return mAudioManager.getRingerMode();
			}
		 private void setSilentMode()  
			    {  
			 switch (getSilentStatus())  
		        {  
		        case AudioManager.RINGER_MODE_SILENT:  
		            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); 
		            Toast.makeText(getApplicationContext(), "振动", Toast.LENGTH_SHORT).show();
		            break;  
		        case AudioManager.RINGER_MODE_NORMAL:  
		            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); 
		            Toast.makeText(getApplicationContext(), "静音", Toast.LENGTH_SHORT).show();
		            break;        
		        case AudioManager.RINGER_MODE_VIBRATE:  
		            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  
		            Toast.makeText(getApplicationContext(), "正常", Toast.LENGTH_SHORT).show();
		            break;  
		        }  
			      

			    } 
			 

	@Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        //ע��㲥������  
        registerReceiver( null, mIntentFilter);  
    }  


	}

