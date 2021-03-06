package com.elgubbo.tastekid.listener;

import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.SectionFragment;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.TasteKidApp;
import com.elgubbo.tastekid.adapter.RecentSearchesArrayAdapter;
import com.elgubbo.tastekid.adapter.SectionsPagerAdapter;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.elgubbo.tastekid.model.ResultManager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

public class SearchQueryChangeListener implements
		SearchView.OnQueryTextListener {

	int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private SparseArray<String> lastSearches;

	public SearchQueryChangeListener(
			SectionsPagerAdapter mSectionsPagerAdapter, int position,
			ViewPager mViewPager) {
		this.mSectionsPagerAdapter = mSectionsPagerAdapter;
		this.position = position;
		this.mViewPager = mViewPager;
		this.lastSearches = new SparseArray<String>();
	}

	/* (non-Javadoc)
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText.length() > 2) {

		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit(java.lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit(String query) {
		TasteKidActivity tasteKidActivity = (TasteKidActivity) TasteKidActivity.getActivityInstance();
		MenuItem searchItem = tasteKidActivity.getMenu().findItem(R.id.action_search);
		searchItem.collapseActionView();
		
		RecentSearchesArrayAdapter recentsAdapter = (RecentSearchesArrayAdapter) tasteKidActivity.getRecentListView().getAdapter();
		recentsAdapter.clear();
		recentsAdapter.addAll(ResultManager.getInstance().getRecentSearches());
		recentsAdapter.notifyDataSetChanged();

		InputMethodManager inputManager = 
		        (InputMethodManager) TasteKidActivity.getAppContext().
		            getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(
		        ((Activity) TasteKidActivity.getActivityInstance()).getCurrentFocus().getWindowToken(),
		        InputMethodManager.HIDE_NOT_ALWAYS); 
		SectionFragment currentFragment = (SectionFragment) mSectionsPagerAdapter
				.getActiveFragment(mViewPager, mViewPager.getCurrentItem());
		TasteKidApp.setCurrentQuery(query);
		this.position = currentFragment.getPosition();
		if(lastSearches.get(position, "").equals(query)){
			return false;
		}
		if (query.trim().equalsIgnoreCase("")) {
			if (Configuration.DEVMODE)
				Log.d("TasteKid", "onQueryTextSubmit doing nothing");
			return false;
		}

		currentFragment.showLoadingBar();

		String type = TasteKidApp.TYPE_ARRAY[position];
		
		if (Configuration.DEVMODE) {
			Log.d("TasteKid", "Position is:" + this.position);
			Log.d("TasteKid", "type is: " + type);
		}
		ResultManager.getInstance().sendResultsForQueryTo((IResultsReceiver) currentFragment, query);

		lastSearches.append(position, query);
		return true;
	}

}
