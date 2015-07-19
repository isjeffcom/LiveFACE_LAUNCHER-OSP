package cc.flydev.launcher;



import cc.flydev.face.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebVIew extends Activity {
	private WebView wb1;
	private ProgressBar pb1;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
			setContentView(R.layout.webview_layout);
			wb1=(WebView) findViewById(R.id.webView1);
			pb1=(ProgressBar) findViewById(R.id.progressBar1);
			wb1.getSettings().setJavaScriptEnabled(true);

			wb1.setWebViewClient(new WebViewClient(){
				
			      @Override
			      public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    	  view.loadUrl(url); 
			          return true;
			      }
			      
			  });
			 wb1.loadUrl("http://www.flydev.cc");
			 wb1.setWebChromeClient(new WebChromeClient() {

		          @Override
		          public void onProgressChanged(WebView view, int newProgress) {
		              if (newProgress == 100) {
		                 pb1.setVisibility(View.INVISIBLE);
		              } else {
		                  if (View.INVISIBLE == pb1.getVisibility()) {
		                      pb1.setVisibility(View.VISIBLE);
		                  }
		                  pb1.setProgress(newProgress);
		              }
		              super.onProgressChanged(view, newProgress);
		          }
		          
		      });


		}
		public boolean onKeyDown(int keyCode, KeyEvent event) {       
	        if ((keyCode == KeyEvent.KEYCODE_BACK) &&   wb1 .canGoBack()) {       
	            wb1.goBack();       
	                   return true;       
	        }       
	        return super.onKeyDown(keyCode, event);       
	    }
		
	}
