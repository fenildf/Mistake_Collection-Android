package org.papdt.miscol.ui;

import org.papdt.miscol.R;
import org.papdt.miscol.ui.adapter.DrawerAdapter;
import org.papdt.miscol.ui.adapter.DrawerAdapter.IDrawerNames;
import org.papdt.miscol.ui.fragment.FragmentAbout;
import org.papdt.miscol.ui.fragment.FragmentConstruction;
import org.papdt.miscol.ui.fragment.FragmentMain;
import org.papdt.miscol.ui.fragment.FragmentCategories;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * App的主Activity <br />
 * 初始化抽屉和首页
 */
public class ActivityMain extends Activity implements IDrawerNames {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private RelativeLayout mRlDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private Fragment[] mFragments;
	private FragmentTransaction mTransaction;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerItemNames;
	private FragmentManager mFragmentManager;
	private MistakesTabListener mTabListener;
	private int mCurrentFragment;

	private final static String TAG = "ActivityMain";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDrawerItemNames = getResources().getStringArray(R.array.drawer_items);
		mFragmentManager = getFragmentManager();
		mFragments = new Fragment[mDrawerItemNames.length];
		initializeDrawer();
		if (savedInstanceState == null) {
			selectItem(MAIN);
			// 默认打开FragmentMain
		}
		Log.d(TAG, TAG + "已完成初始化");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 在不同的Fragment中分开处理
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		return super.onOptionsItemSelected(item);
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing mFragments
		mCurrentFragment = position;
		boolean initialized;
		mTransaction = mFragmentManager.beginTransaction();
		for (Fragment fragment : mFragments) {
			hideFragment(fragment);
		}
		if (mFragments[position] == null) {
			Log.d(TAG, "创建新的 Fragment:" + position);
			initialized = false;
			switch (position) {
			case MAIN:
				mFragments[position] = FragmentMain.getInstance();
				break;
			case MISTAKES:
				mFragments[position] = FragmentCategories.getInstance();
				break;
			case ABOUT:
				mFragments[position] = new FragmentAbout();
				break;
			default:
				mFragments[position] = new FragmentConstruction();
				// TODO 初始化各Fragment
				break;
			}
		} else {
			Log.d(TAG, "已存在Fragment:" + position);
			initialized = true;
		}
		replaceToFragment(mFragments[position], initialized, TAGS[position]);
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mRlDrawer);
	}

	private void replaceToFragment(Fragment fragment, boolean hasInitialized,
			String tag) {
		if (!hasInitialized) {
			mTransaction.add(R.id.fl_content, fragment, tag);
		}
		mTransaction.attach(fragment).show(fragment).commit();
		mFragmentManager.popBackStack();
		mFragmentManager.executePendingTransactions();
		if (tag.equals(TAGS[MISTAKES]))
			initializeTabs();
	}

	private void hideFragment(Fragment fragment) {
		if (fragment != null) {
			mTransaction.hide(fragment);
			if (fragment.getTag().equals(TAGS[MISTAKES]))
				removeTabs();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * 初始化NavigationDrawer <br />
	 * 向ListView中填充程序导航模块
	 */
	private void initializeDrawer() {

		mTitle = getTitle();
		mDrawerTitle = getString(R.string.drawer_title);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
		mDrawerList = (ListView) findViewById(R.id.lv_drawer);
		mRlDrawer = (RelativeLayout) findViewById(R.id.rl_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new DrawerAdapter(mDrawerItemNames, this));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {

			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				if (mCurrentFragment == MAIN && mFragments[MAIN] != null)
					((FragmentMain) mFragments[MAIN]).showHint();
			}



			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				if (mCurrentFragment == MAIN && mFragments[MAIN] != null) {
					((FragmentMain) mFragments[MAIN]).hideHint();
				}

			}

			
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void initializeTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		if (mTabListener == null) {
			mTabListener = new MistakesTabListener(
					(FragmentCategories) mFragments[MISTAKES]);
		}
		//FIXME 横屏模式下ActionBar Tab颜色问题
		actionBar.removeAllTabs();
		Tab tagTab = actionBar.newTab().setText(R.string.tag)
				.setTag(MistakesTabListener.TAGS).setTabListener(mTabListener);
		Tab gradeTab = actionBar.newTab().setText(R.string.subject_or_grade)
				.setTag(MistakesTabListener.SUBJECTS)
				.setTabListener(mTabListener);
		actionBar.addTab(tagTab);
		actionBar.addTab(gradeTab);

	}

	private void removeTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.removeAllTabs();
	}

}
