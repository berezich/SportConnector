package com.berezich.sportconnector.SpotInfo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.berezich.sportconnector.EndpointApi;
import com.berezich.sportconnector.ErrorVisualizer;
import com.berezich.sportconnector.GoogleMap.SpotsData;
import com.berezich.sportconnector.LocalDataManager;
import com.berezich.sportconnector.R;
import com.berezich.sportconnector.backend.sportConnectorApi.model.Person;
import com.berezich.sportconnector.backend.sportConnectorApi.model.RegionInfo;
import com.berezich.sportconnector.backend.sportConnectorApi.model.Spot;
import com.berezich.sportconnector.backend.sportConnectorApi.model.UpdateSpotInfo;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpotInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpotInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotInfoFragment extends Fragment implements EndpointApi.GetListPersonByIdLstAsyncTask.OnAction,
                                                          /*EndpointApi.UpdateSpotAsyncTask.OnAction,*/
                                                          EndpointApi.SetSpotAsFavoriteAsyncTask.OnAction
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SPOT_ID = "spotId";
    private static final  String TAB_PARTNERS = "partners";
    private static final  String TAB_COACHES = "coaches";
    private static final String TAG = "MyLog_SpotInfoFragment";
    // TODO: Rename and change types of parameters
    private Long spotId;
    private boolean isFavoriteChanged=false;
    private HashMap<Long,Spot> spotHashMap;
    private Spot curSpot;
    private View spotInfoView;
    private ProfileItemLstAdapter partnersAdapter;
    private ProfileItemLstAdapter coachesAdapter;
    private ArrayList<String> personIdLst;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpotInfoFragment newInstance(Long spotId) {
        SpotInfoFragment fragment = new SpotInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SPOT_ID, String.valueOf(spotId));
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SpotInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spotId = Long.valueOf(getArguments().getString(ARG_SPOT_ID));
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView txtView;
        TabHost tabHost;
        ListView lstView;
        ImageButton imgButton;
        getActivity().setTitle(R.string.spotinfo_fragmentTitle);
        spotInfoView = inflater.inflate(R.layout.fragment_spot_info, container, false);
        spotHashMap = SpotsData.get_allSpots();
        if((txtView = (TextView) spotInfoView.findViewById(R.id.spotinfo_frg_tryAgain_txtView))!=null)
            txtView.setOnClickListener(new TryAgainClickListener());
        curSpot = spotHashMap.get(spotId);
        if(curSpot!=null)
        {
            if((txtView =(TextView) spotInfoView.findViewById(R.id.spotInfo_txt_name))!=null)
                txtView.setText(curSpot.getName());
            if((txtView =(TextView) spotInfoView.findViewById(R.id.spotInfo_txt_adress))!=null)
                txtView.setText(curSpot.getAddress());
            if((imgButton=(ImageButton) spotInfoView.findViewById(R.id.spotInfo_btnImg_favorite))!=null) {
                imgButton.setPressed(LocalDataManager.isMyFavoriteSpot(curSpot));
                imgButton.setOnTouchListener(new StarBtnOnTouchListener());
            }

            int coachesNum = SpotsData.getCoachIdsWithoutMe(curSpot).size(), partnersNum = SpotsData.getPartnerIdsWithoutMe(curSpot).size();
            if((partnersNum>0 || coachesNum>0)&&(tabHost = (TabHost) spotInfoView.findViewById(R.id.spotInfo_tabHost))!=null)
            {
                // инициализация
                tabHost.setup();

                TabHost.TabSpec tabSpec;
                if(partnersNum>0) {
                    // создаем вкладку и указываем тег
                    tabSpec = tabHost.newTabSpec(TAB_PARTNERS);
                    // название вкладки
                    tabSpec.setIndicator(getString(R.string.spotinfo_tab1_title));

                    lstView = (ListView) spotInfoView.findViewById(R.id.spotInfo_list_tab_partners);
                    /*partnersAdapter = new ProfileItemLstAdapter(getActivity().getApplicationContext(),
                            new ArrayList<Person>(curSpot.getPartnerLst()));
                    lstView.setAdapter(partnersAdapter);*/

                    // указываем id компонента из FrameLayout, он и станет содержимым
                    tabSpec.setContent(R.id.spotInfo_list_tab_partners);
                    // добавляем в корневой элемент
                    tabHost.addTab(tabSpec);
                }
                if(coachesNum>0 ) {
                    tabSpec = tabHost.newTabSpec(TAB_COACHES);
                    tabSpec.setIndicator(getString(R.string.spotinfo_tab2_title));
                    /*
                    lstView = (ListView) spotInfoView.findViewById(R.id.spotInfo_list_tab_coaches);
                    coachesAdapter = new ProfileItemLstAdapter(getActivity().getApplicationContext(),
                            new ArrayList<Person>(curSpot.getCoachLst()));
                    lstView.setAdapter(coachesAdapter);*/
                    tabSpec.setContent(R.id.spotInfo_list_tab_coaches);
                    tabHost.addTab(tabSpec);
                }
                //tabHost.setCurrentTabByTag(TAB_PARTNERS);

    /*
                // обработчик переключения вкладок
                tabHost.setOnTabChangedListener(new OnTabChangeListener() {
                    public void onTabChanged(String tabId) {
                        Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
                    }
                });
    */
            }
            else {
                setVisible(View.GONE, View.GONE, View.VISIBLE);
                return spotInfoView;
            }
            /*else if((txtView =(TextView) spotInfoView.findViewById(R.id.spotInfo_txt_noPartersCoaches))!=null) {
                if((tabHost = (TabHost) spotInfoView.findViewById(R.id.spotInfo_tabHost))!=null)
                    tabHost.setVisibility(View.GONE);
                txtView.setVisibility(View.VISIBLE);
            }*/


            personIdLst = new ArrayList<String>();
            if(curSpot.getCoachLst()!=null)
                personIdLst.addAll(SpotsData.getCoachIdsWithoutMe(curSpot));
            if(curSpot.getPartnerLst()!=null)
                personIdLst.addAll(SpotsData.getPartnerIdsWithoutMe(curSpot));
            if(personIdLst.size()>0) {
                setVisibleProgressBar();
                new EndpointApi.GetListPersonByIdLstAsyncTask(this).execute(personIdLst);

            }
        }
        return spotInfoView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if(isFavoriteChanged) {

            //new EndpointApi.UpdateSpotAsyncTask(this).execute(spotHashMap.get(curSpot.getId()));


        }*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class StarBtnOnTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton btn = (ImageButton) v;
            // show interest in events resulting from ACTION_DOWN
            if(event.getAction()==MotionEvent.ACTION_DOWN) {
                btn.setPressed(!btn.isPressed());
                isFavoriteChanged = !isFavoriteChanged;
                SpotsData.setSpotFavorite(curSpot.getId(), btn.isPressed());
                new EndpointApi.SetSpotAsFavoriteAsyncTask(getFragmentRef()).execute(new Pair<Long, String>(curSpot.getId(),
                        LocalDataManager.getMyPersonInfo().getId()),new Pair<Long, String>(btn.isPressed()? new Long(1) :new Long(0),
                        LocalDataManager.getMyPersonInfo().getType()));
                return true;
            }
            return true;
        }
    }

    @Override
    public void onGetListPersonByIdLstFinish(Pair<List<Person>, Exception> result) {
        List<Person> personLst = result.first;
        Exception error = result.second;
        ListView lstView;
        Person person;
        List<Person> coachLst = new ArrayList<>();
        List<Person> partnerLst = new ArrayList<>();
        if(getActivity()==null)
        {
            Log.e(TAG,"current fragment isn't attached to activity");
            return;
        }
        if(error == null && personLst!=null)
        {
            for (int i = 0; i <personLst.size() ; i++) {
                person = personLst.get(i);
                if(person.getType().equals("COACH"))
                    coachLst.add(person);
                if(person.getType().equals("PARTNER"))
                    partnerLst.add(person);
            }
            lstView = (ListView) spotInfoView.findViewById(R.id.spotInfo_list_tab_coaches);
            coachesAdapter = new ProfileItemLstAdapter(getActivity().getApplicationContext(),
                    new ArrayList<Person>(coachLst));
            lstView.setAdapter(coachesAdapter);
            lstView = (ListView) spotInfoView.findViewById(R.id.spotInfo_list_tab_partners);
            partnersAdapter = new ProfileItemLstAdapter(getActivity().getApplicationContext(),
                    new ArrayList<Person>(partnerLst));
            lstView.setAdapter(partnersAdapter);

            if(partnerLst.size()>0 || coachLst.size()>0)
                setVisible(View.VISIBLE,View.GONE,View.GONE);
            else
                setVisible(View.GONE,View.GONE,View.VISIBLE);

            return;
        }
        Log.e(TAG, "Error GetListPersonByIdLst");
        if(personLst==null)
            Log.e(TAG,"personLst = null");

        FrameLayout frameLayout;
        if((frameLayout = (FrameLayout) spotInfoView.findViewById(R.id.spotinfo_frg_frameLayout))!=null)
            ErrorVisualizer.showErrorAfterReq(getActivity().getBaseContext(), frameLayout, error, TAG);
        setVisible(View.GONE,View.VISIBLE,View.GONE);

    }

    /*@Override
    public void onUpdateSpotFinish(Pair<Spot, Exception> result) {
        Spot spot = result.first;
        Exception error = result.second;
        if(error == null && spot!=null) {
            Log.d(TAG, "Spot updated success");
            return;
        }
            Log.e(TAG, "Error UpdateSpot");
        if(error!=null)
        {
            Log.e(TAG, error.getMessage());
            error.printStackTrace();
        }
        else
            Log.d(TAG,"UpdatedSpot = null");
    }
*/
    @Override
    public void onSetSpotAsFavoriteFinish(Pair<Boolean,Exception> result) {
        boolean isFavorite = result.first;
        Exception ex = result.second;

        if(ex!=null)
        {
            if(spotInfoView!=null) {
                ImageButton btn = (ImageButton) spotInfoView.findViewById(R.id.spotInfo_btnImg_favorite);
                if (btn!=null) {
                    if(btn.isPressed()==isFavorite)
                        btn.setPressed(!isFavorite);
                    SpotsData.setSpotFavorite(curSpot.getId(), !isFavorite);
                    Toast.makeText(getActivity().getBaseContext(), R.string.spotinfo_req_error_msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private class TryAgainClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if(personIdLst.size()>0) {
                setVisibleProgressBar();
                new EndpointApi.GetListPersonByIdLstAsyncTask(getFragmentRef()).execute(personIdLst);

            }
        }
    }
    private void setVisible(int tabsVisibility, int frameLayoutVisibility, int noPersonsVisibility)
    {
        FrameLayout frameLayout1;
        View view;
        if(spotInfoView!=null) {
            if ((frameLayout1 = (FrameLayout) spotInfoView.findViewById(R.id.spotinfo_frg_frameLayout)) != null)
                frameLayout1.setVisibility(frameLayoutVisibility);
            if ((view = spotInfoView.findViewById(R.id.spotInfo_tabHost)) != null)
                view.setVisibility(tabsVisibility);
            if ((view = spotInfoView.findViewById(R.id.spotInfo_txt_noPartersCoaches)) != null)
                view.setVisibility(noPersonsVisibility);
        }
    }
    private void setVisibleProgressBar()
    {
        FrameLayout frameLayout;
        setVisible(View.GONE,View.VISIBLE,View.GONE);

        if((frameLayout = (FrameLayout) spotInfoView.findViewById(R.id.spotinfo_frg_frameLayout))!=null)
            ErrorVisualizer.showProgressBar(frameLayout);
    }
    private Fragment getFragmentRef()
    {
        return  this;
    }
}
