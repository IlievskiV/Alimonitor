package ch.cern.alice.alimonalisa.fragments;

import ch.cern.alice.alimonalisa.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


//Fragment on the top of the screen
public class TopFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.top_fragment, container, false);
		return view;
	}
	
}
