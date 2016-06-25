package net.usrlib.pocketbuddha.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.usrlib.pocketbuddha.R;

/**
 * Created by rgr-myrg on 6/11/16.
 */
public class BaseActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	protected void initContentView(final int resource) {
		setContentView(resource);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		initDrawerLayout(toolbar);
		initNavigationView();
	}

	protected void initDrawerLayout(Toolbar toolbar) {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);

		if (drawer == null) {
			return;
		}

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawer,
				toolbar,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		);

		drawer.addDrawerListener(toggle);
		toggle.syncState();
	}

	protected void initNavigationView() {
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView != null) {
			navigationView.setNavigationItemSelectedListener(this);
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);

		if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(
						new ComponentName(this, DetailActivity.class)
				)
		);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();

		switch (itemId) {
			case R.id.action_home:
				startActivity(new Intent(this, HomeActivity.class));
				break;
			case R.id.action_favorites:
				startActivity(new Intent(this, FavoritesActivity.class));
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		final int itemId = item.getItemId();

		switch (itemId) {
			case R.id.nav_about:
				startActivity(new Intent(this, AboutActivity.class));
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		return true;
	}
}
