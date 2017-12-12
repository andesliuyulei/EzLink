package com.keysight.yuleil01.ezlink;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static GoogleAccountCredential accountCredential;
    ProgressDialog progressDialog;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/spreadsheets"
    };

    public static final String EZLINK_CARD_NUMBER = "EZLink Card Number";
    public static final String TRANSPORTATION_TYPE = "MRT or BUS";
    public static final String MRT_STATION_FROM = "MRT Station (From)";
    public static final String MRT_STATION_TO = "MRT Station (To)";
    public static final String BUS_NUMBER = "Bus Number";
    public static final String BUS_STOP_FROM = "Bus Stop (From)";
    public static final String BUS_STOP_TO = "Bus Stop (To)";
    public static final String EZLINK_RESULT1 = "EZLink Result 1";
    public static final String EZLINK_RESULT2 = "EZLink Result 2";
    public static final String EZLINK_RESULT3 = "EZLink Result 3";
    public static final String EZLINK_RESULT4 = "EZLink Result 4";

    // ID of the script to call. Acquire this from the Apps Script editor,
    // under Publish > Deploy as API executable.
    private static String scriptId_EzLink = "M3xs1SNwyea50RwmMHfYiXkw9ezPKz0cG"; //ezlink

    private RadioGroup radioGroup;
    private RadioButton mrtRadio, busRadio;
    private AutoCompleteTextView ezlinkCardNumber, mrtFrom, mrtTo;
    private EditText busNumber, busFrom, busTo, fareSgd;
    private Button submit;
    private static String transportationType = "MRT";

    private static int initJobCount = 0;
    private static List<String> knownFareList_MrtMrt = null;
    private static List<String> ezLinkCardNumbers = null;
    private static List<String> ezLinkCardTypes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ezlinkCardNumber = findViewById(R.id.editEZLinkCardNumber);
        radioGroup = findViewById(R.id.selectMRTorBUS);
        mrtRadio = findViewById(R.id.radioButtonMRT);
        busRadio = findViewById(R.id.radioButtonBUS);
        mrtFrom = findViewById(R.id.editMRT1);
        mrtTo = findViewById(R.id.editMRT2);
        busNumber = findViewById(R.id.editBusNumber);
        busFrom = findViewById(R.id.editBusStop1);
        busTo = findViewById(R.id.editBusStop2);
        fareSgd = findViewById(R.id.editFareSgd);
        submit = findViewById(R.id.buttonSubmit);

        busNumber.setVisibility(View.GONE);
        busFrom.setVisibility(View.GONE);
        busTo.setVisibility(View.GONE);
        fareSgd.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (mrtRadio.isChecked() == true) {
                    mrtFrom.setVisibility(View.VISIBLE);
                    mrtTo.setVisibility(View.VISIBLE);
                    busNumber.setVisibility(View.GONE);
                    busFrom.setVisibility(View.GONE);
                    busTo.setVisibility(View.GONE);
                    transportationType = "MRT";
                } else {
                    mrtFrom.setVisibility(View.GONE);
                    mrtTo.setVisibility(View.GONE);
                    busNumber.setVisibility(View.VISIBLE);
                    busFrom.setVisibility(View.VISIBLE);
                    busTo.setVisibility(View.VISIBLE);
                    transportationType = "BUS";
                }
            }
        });

        // Initialize credentials and service object..
        accountCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        progressDialog = new ProgressDialog(this);
        initializeDataFromApi();
    }

    /**
     * @description Get EzLink card type of a EzLink card number.
     * @param ezLinkCardNumber
     * @return
     */
    private String getEzlinkCardType(String ezLinkCardNumber) {
        return ezLinkCardTypes.get(ezLinkCardNumbers.indexOf(ezLinkCardNumber));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.copyto_8657:
                fareSgd.setText("");
                fareSgd.setVisibility(View.GONE);
                ezlinkCardNumber.setText("1009622008118657");
                getResultsFromApi();
                break;
            case R.id.copyto_8910:
                fareSgd.setText("");
                fareSgd.setVisibility(View.GONE);
                ezlinkCardNumber.setText("8009150000708910");
                getResultsFromApi();
                break;
            case R.id.sbw_yis:
                ezlinkCardNumber.setText("1009622003582322");
                mrtRadio.setChecked(Boolean.TRUE);
                mrtFrom.setText("Sembawang");
                mrtTo.setText("Yishun");
                getResultsFromApi();
                break;
            case R.id.yis_sbw:
                ezlinkCardNumber.setText("1009622003582322");
                mrtRadio.setChecked(Boolean.TRUE);
                mrtFrom.setText("Yishun");
                mrtTo.setText("Sembawang");
                getResultsFromApi();
                break;
            case R.id.rst_form:
                ezlinkCardNumber.setText("");
                if (mrtRadio.isChecked()) {
                    mrtFrom.setText("");
                    mrtTo.setText("");
                } else {
                    busNumber.setText("");
                    busFrom.setText("");
                    busTo.setText("");
                }
                fareSgd.setVisibility(View.GONE);
                break;
            case R.id.init_data:
                initializeDataFromApi();
                break;
            default:
                //do nothing here!
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @description //TODO need to revisit this function.
     * @param msg
     */
    private void alert(String msg) {
        AlertDialog.Builder abuilder = new AlertDialog.Builder(this);
        abuilder.setMessage(msg);
        abuilder.setCancelable(Boolean.FALSE);
        abuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = abuilder.create();
        alert11.show();
    }

    private void initializeDataFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (accountCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            alert("No network connection available.");
        } else {
            initJobCount = 0;
            new MakeRequestTask(accountCredential, scriptId_EzLink, "getInfo_ActiveEzLinkCards", null).execute();
            new MakeRequestTask(accountCredential, scriptId_EzLink, "getListOfRailStations", null).execute();
            new MakeRequestTask(accountCredential, scriptId_EzLink, "getInfo_KnownFareLookup", null).execute();
        }
    }

    /** Called when the user taps the Submit Transaction button */
    public void submitTransaction(View view) {
        submit.setEnabled(Boolean.FALSE);
        getResultsFromApi();
        submit.setEnabled(Boolean.TRUE);
    }

    private void displayResult(List<String> output) {
        Intent intent = new Intent(this, PerformEZLinkTransaction.class);
        intent.putExtra(EZLINK_CARD_NUMBER, ezlinkCardNumber.getText().toString());
        intent.putExtra(TRANSPORTATION_TYPE, transportationType);
        intent.putExtra(MRT_STATION_FROM, mrtFrom.getText().toString());
        intent.putExtra(MRT_STATION_TO, mrtTo.getText().toString());
        intent.putExtra(BUS_NUMBER, busNumber.getText().toString());
        intent.putExtra(BUS_STOP_FROM, busFrom.getText().toString());
        intent.putExtra(BUS_STOP_TO, busTo.getText().toString());
        if (output != null) {
            intent.putExtra(EZLINK_RESULT1, output.toArray()[0].toString());
            intent.putExtra(EZLINK_RESULT2, output.toArray()[1].toString());
            intent.putExtra(EZLINK_RESULT3, output.toArray()[2].toString());
            intent.putExtra(EZLINK_RESULT4, output.toArray()[3].toString());
        }
        startActivity(intent);
    }

    /**
     * Description: Get the fare of MRT to MRT route.
     * /
    private float getFare_Mrt_Mrt(String mrt1, String mrt2) {
        for (int i=0; i<knownFareTable.length; i++) {
            if (knownFareTable[i][0].equals("MRT-MRT")) {
                if (knownFareTable[i][1].equals(mrt1) && knownFareTable[i][2].equals(mrt2)) {
                    return Float.parseFloat(knownFareTable[i][3]);
                } else if (knownFareTable[i][1].equals(mrt2) && knownFareTable[i][2].equals(mrt1)) {
                    return Float.parseFloat(knownFareTable[i][3]);
                }
            }
        }
        return 0;
    }//*/

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        String transactionCardNumber = ezlinkCardNumber.getText().toString();
        if (transactionCardNumber.equals("")) {
            alert("Please enter 'EZLink Card Number'.");
            return;
        }
        String transactionCardType = getEzlinkCardType(transactionCardNumber);

        /*//not enabled yet.
        Date now = new Date();
        int day = now.getDay();
        int hour = now.getHours();
        int minute = now.getMinutes();
        if (day >= 1 && day <= 5) {
            if (hour < 7) {
                transactionCardType = transactionCardType.concat("/off-peak");
            } else if (hour == 7 && minute < 45) {
                transactionCardType = transactionCardType.concat("/off-peak");
            } else {
                //do nothing here.
            }
        } else {
            //do nothing here.
        }//*/

        /*//Requires API level 26.
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        if (today.equals(DayOfWeek.MONDAY) ||
                today.equals(DayOfWeek.TUESDAY) ||
                today.equals(DayOfWeek.WEDNESDAY) ||
                today.equals(DayOfWeek.THURSDAY) ||
                today.equals(DayOfWeek.FRIDAY)) {
            if (hour < 7) {
                transactionCardType = transactionCardType.concat("(off-peak)");
            } else if (hour == 7 && minute < 45) {
                transactionCardType = transactionCardType.concat("(off-peak)");
            } else {
                //do nothing here.
                transactionCardType = transactionCardType.concat("(off-peak)");
            }
        }//*/

        String functionName = "";
        List<Object> functionParameters = new ArrayList<>();

        functionParameters.add(transactionCardNumber);
        if (mrtRadio.isChecked()) {
            if (mrtFrom.getText().toString().equals("")) {
                alert("Please enter 'MRT Station (From)'.");
                return;
            } else if (mrtTo.getText().toString().equals("")) {
                alert("Please enter 'MRT Station (To)'.");
                return;
            }

            String mrt1 = mrtFrom.getText().toString();
            String mrt2 = mrtTo.getText().toString();
            functionParameters.add(mrt1);
            functionParameters.add(mrt2);
            if (knownFareList_MrtMrt.indexOf(mrt1 + "|" + mrt2 + "|" + transactionCardType) >= 0) {
                functionName = "ezlinkTransaction_MrtMrt";
            } else {
                if (!fareSgd.isShown()) {
                    alert("The fare (" + mrt1 + "-" + mrt2 + "-" + transactionCardType + ") is unknown. Please enter 'Fare (SGD)'.");
                    fareSgd.setText("");
                    fareSgd.setVisibility(View.VISIBLE);
                    return;
                } else if (fareSgd.getText().toString().equals("")) {
                    alert("Please enter 'Fare (SGD)'.");
                    return;
                }
                functionName = "ezlinkTransaction_MrtMrt_NewFare";
                functionParameters.add(Float.parseFloat(fareSgd.getText().toString()));
                knownFareList_MrtMrt.add(mrt1 + "|" + mrt2 + "|" + transactionCardType);
                knownFareList_MrtMrt.add(mrt2 + "|" + mrt1 + "|" + transactionCardType);
            }
        } else {
            functionName = "ezlinkTransaction_Bus";
            functionParameters.add(busNumber.getText().toString());
            functionParameters.add(busFrom.getText().toString());
            functionParameters.add(busTo.getText().toString());
        }

        if (! isDeviceOnline()) {
            alert("No network connection available.");
            return;
        } else {
            new MakeRequestTask(accountCredential, scriptId_EzLink, functionName, functionParameters).execute();
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                accountCredential.setSelectedAccountName(accountName);
                //getResultsFromApi();
                initializeDataFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        accountCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * An asynchronous task that handles the Google Apps Script Execution API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.script.Script mService = null;
        private Exception mLastError = null;
        //private String exeState = null;
        private String scriptId = null;
        private String functionName = null;
        private List<Object> functionParameters = null;

        MakeRequestTask(GoogleAccountCredential credential, String script, String function, List<Object> parameters) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            //exeState = state;
            scriptId = script;
            functionName = function;
            functionParameters = parameters;
            mService = new com.google.api.services.script.Script.Builder(
                    transport, jsonFactory, setHttpTimeout(credential))
                    .setApplicationName("EZLink")
                    .build();
        }

        /**
         * Background task to call Google Apps Script Execution API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Call the API to run an Apps Script function that returns a list
         * of folders within the user's root directory on Drive.
         *
         * @return list of String folder names and their IDs
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException, GoogleAuthException {
            // Create an execution request object.
            ExecutionRequest request = new ExecutionRequest()
                    .setDevMode(Boolean.TRUE) //TODO: remove dev mode before release.
                    .setParameters(functionParameters)
                    .setFunction(functionName);

            // Make the request.
            Operation op = mService.scripts().run(scriptId, request).execute();

            // Print results of request.
            if (op.getError() != null) {
                throw new IOException(getScriptError(op));
            }

            if (op.getResponse() != null && op.getResponse().get("result") != null) {
                return (List<String>) op.getResponse().get("result");
            } else {
                return null;
            }
        }

        /**
         * Interpret an error response returned by the API and return a String
         * summary.
         *
         * @param op the Operation returning an error response
         * @return summary of error response, or null if Operation returned no
         *     error
         */
        private String getScriptError(Operation op) {
            if (op.getError() == null) {
                return null;
            }

            // Extract the first (and only) set of error details and cast as a Map.
            // The values of this map are the script's 'errorMessage' and
            // 'errorType', and an array of stack trace elements (which also need to
            // be cast as Maps).
            Map<String, Object> detail = op.getError().getDetails().get(0);
            List<Map<String, Object>> stacktrace =
                    (List<Map<String, Object>>)detail.get("scriptStackTraceElements");

            java.lang.StringBuilder sb =
                    new StringBuilder("\nScript error message: ");
            sb.append(detail.get("errorMessage"));

            if (stacktrace != null) {
                // There may not be a stacktrace if the script didn't start
                // executing.
                sb.append("\nScript error stacktrace:");
                for (Map<String, Object> elem : stacktrace) {
                    sb.append("\n  ");
                    sb.append(elem.get("function"));
                    sb.append(":");
                    sb.append(elem.get("lineNumber"));
                }
            }
            sb.append("\n");
            return sb.toString();
        }

        @Override
        protected void onPreExecute() {
            if (functionName.equals("getInfo_fActiveCards") || functionName.equals("getListOfRailStations") || functionName.equals("getInfo_KnownFareLookup")) {
                progressDialog.setMessage("Initializing data from the backend system ...");
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(Boolean.FALSE);
                }
            } else {
                progressDialog.setMessage("Performing EZLink transaction in the backend system ...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(Boolean.FALSE);
            }
        }

        @Override
        protected void onPostExecute(List<String> output) {
            String[] titles, cardInfo, fareInfo;
            switch (functionName) {
                case "getInfo_ActiveEzLinkCards":
                    ezLinkCardNumbers = new ArrayList<>();
                    ezLinkCardTypes = new ArrayList<>();
                    titles = output.get(0).split("\\|");
                    int indexof_EzLinkCardNumber = 0;
                    int indexof_EzLinkCardType = 0;

                    //the first row are the titles.
                    for (int i=0; i<titles.length; i++) {
                        if (titles[i].equals("Card Number")) {
                            indexof_EzLinkCardNumber = i;
                        } else if (titles[i].equals("Card Type")) {
                            indexof_EzLinkCardType = i;
                        } else {
                            //do nothing here.
                        }
                    }

                    for (int i=1; i<output.size(); i++) {
                        cardInfo = output.get(i).split("\\|");
                        ezLinkCardNumbers.add(cardInfo[indexof_EzLinkCardNumber]);
                        ezLinkCardTypes.add(cardInfo[indexof_EzLinkCardType]);
                    }

                    ezlinkCardNumber.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, ezLinkCardNumbers));
                    initJobCount++;
                    if (initJobCount >= 3) {
                        progressDialog.dismiss();
                    }
                    break;
                case "getListOfRailStations":
                    String[] listOfRailStations = output.toArray(new String[0]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listOfRailStations);
                    mrtFrom.setAdapter(adapter);
                    mrtTo.setAdapter(adapter);
                    initJobCount++;
                    if (initJobCount >= 3) {
                        progressDialog.dismiss();//.hide();
                    }
                    break;
                case "getInfo_KnownFareLookup":
                    knownFareList_MrtMrt = new ArrayList<>();
                    titles = output.get(0).split("\\|");
                    int indexof_Route = 0;
                    int indexof_Mrt1 = 0;
                    int indexof_Mrt2 = 0;
                    int indexof_FareType = 0;

                    //the first row are the titles.
                    for (int i=0; i<titles.length; i++) {
                        switch (titles[i]) {
                            case "Route":
                                indexof_Route = i;
                                break;
                            case "MRT 1":
                                indexof_Mrt1 = i;
                                break;
                            case "MRT 2":
                                indexof_Mrt2 = i;
                                break;
                            case "Fare Type":
                                indexof_FareType = i;
                                break;
                            default:
                                //do nothing here.
                                break;
                        }
                    }

                    for (int i=1; i<output.size(); i++) {
                        fareInfo = output.get(i).split("\\|");
                        if (fareInfo[indexof_Route].equals("MRT-MRT")) {
                            knownFareList_MrtMrt.add(fareInfo[indexof_Mrt1] + "|" + fareInfo[indexof_Mrt2] + "|" + fareInfo[indexof_FareType]);
                            knownFareList_MrtMrt.add(fareInfo[indexof_Mrt2] + "|" + fareInfo[indexof_Mrt1] + "|" + fareInfo[indexof_FareType]);
                        }
                    }

                    initJobCount++;
                    if (initJobCount >= 3) {
                        progressDialog.dismiss();//.hide();
                    }
                    break;
                default:
                    progressDialog.dismiss();
                    displayResult(output);
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    //resultMsg = mLastError.getMessage();
                    //mOutputText.setText("The following error occurred:\n"
                    //        + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Extend the given HttpRequestInitializer (usually a credentials object)
     * with additional initialize() instructions.
     *
     * @param requestInitializer the initializer to copy and adjust; typically
     *         a credential object.
     * @return an initializer with an extended read timeout.
     */
    private static HttpRequestInitializer setHttpTimeout(
            final HttpRequestInitializer requestInitializer) {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest)
                    throws java.io.IOException {
                requestInitializer.initialize(httpRequest);
                // This allows the API to call (and avoid timing out on)
                // functions that take up to 6 minutes to complete (the maximum
                // allowed script run time), plus a little overhead.
                httpRequest.setReadTimeout(380000);
            }
        };
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    //mOutputText.setText(
                    //        "This app requires Google Play Services. Please install " +
                    //                "Google Play Services on your device and relaunch this app.");
                } else {
                    //getResultsFromApi();
                    initializeDataFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        accountCredential.setSelectedAccountName(accountName);
                        //getResultsFromApi();
                        initializeDataFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //getResultsFromApi();
                    initializeDataFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    //@Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    //@Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }
}
