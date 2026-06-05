package com.distribuidora.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class TabsPagerAdapter extends FragmentPagerAdapter {

	String idprofesional;
	
	public TabsPagerAdapter(FragmentManager fm, String idProfesional) {
		super(fm);
		this.idprofesional=idProfesional;
		// TODO Auto-generated constructor stub
	}
	
	public Fragment getItem(int index){
		
		Bundle args = new Bundle();
		args.putString("idprofesional", idprofesional);
		Fragment fragmento;
		
		/*switch (index) {
        case 0:
            // Top Rated fragment activity
        	fragmento= new ProfesionalDetalle();
        	fragmento.setArguments(args);
        	return fragmento;
        case 1:
            // Games fragment activity
        	fragmento=new ProfesionalCentrosSalud();
        	fragmento.setArguments(args);
            return fragmento;
        case 2:
            // Movies fragment activity
        	fragmento= new ProfesionalMutuales();
        	fragmento.setArguments(args);
            return fragmento;
        }*/
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
