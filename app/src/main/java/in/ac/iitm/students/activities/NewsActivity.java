package in.ac.iitm.students.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;

import in.ac.iitm.students.R;
import in.ac.iitm.students.objects.News;


public class NewsActivity extends AppCompatActivity {
    final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout collapsingToolbarLayout = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data = getIntent().getExtras().getString("news");
//        Toast.makeText(this,data,Toast.LENGTH_LONG).show();
        News news = gson.fromJson(data, News.class);
//        TextView textView =(TextView) findViewById(R.id.newsa_title);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // webView.getSettings().setLoadWithOverviewMode(true);

        collapsingToolbarLayout.setTitle(news.getTitle());
        //TODO: change webview
        webView.loadData("<style>img{display: inline; height: auto; max-width: 100%;}</style>" + news.getContent(), "text/html; charset=UTF-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
