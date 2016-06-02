package hbie.vip.parking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import hbie.vip.parking.fragment.CarFragment;
import hbie.vip.parking.fragment.InformationFragment;
import hbie.vip.parking.fragment.MeFragment;
import hbie.vip.parking.fragment.PayFragment;


public class MainActivity extends BaseActivity {
    protected static final String TAG = "MainActivity";
    private TextView unreadInformation, unreadFinding, unreadNews, unreadMe;
    private Button[] mTabs;
    private InformationFragment informationFragment;
    private PayFragment payFragment;
    private CarFragment carFragment;
    private MeFragment meFragment;
    private Fragment[] fragments;
    private int index;
    // 当前fragment的index
    private int currentTabIndex;
    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;

//    private MyConnectionListener connectionListener = null;
//    private UpdateManager mUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        informationFragment = new InformationFragment();
        carFragment = new CarFragment();
        payFragment = new PayFragment();
        meFragment = new MeFragment();
        fragments = new Fragment[] { informationFragment, carFragment, payFragment, meFragment };
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, informationFragment).add(R.id.fragment_container, carFragment).hide(carFragment).show(informationFragment)
                .commit();

    }
    /**
     * 初始化组件
     */
    private void initView() {
        unreadInformation = (TextView) findViewById(R.id.tv_unread_information_msg_number);
        unreadFinding = (TextView) findViewById(R.id.tv_unread_finding_msg_number);
        unreadNews = (TextView) findViewById(R.id.tv_unread_news_msg_number);
        unreadMe = (TextView) findViewById(R.id.tv_unread_me_msg_number);
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_information);
        mTabs[1] = (Button) findViewById(R.id.btn_finding);
        mTabs[2] = (Button) findViewById(R.id.btn_news);
        mTabs[3] = (Button) findViewById(R.id.btn_me);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
        registerForContextMenu(mTabs[1]);
    }
    /**
     * button点击事件
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_information:
                index = 0;
                break;
            case R.id.btn_finding:
                index = 1;
                break;
            case R.id.btn_news:
                index = 2;
                break;
            case R.id.btn_me:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
