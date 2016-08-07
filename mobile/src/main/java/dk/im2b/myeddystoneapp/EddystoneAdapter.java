package dk.im2b.myeddystoneapp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.List;

/**
 * Created by silverbaq on 8/6/16.
 */
public class EddystoneAdapter extends BaseAdapter {

    Activity mActivity;
    List<Eddystone> Eddystone;

    public EddystoneAdapter(Activity activity, List<Eddystone> EddystoneList) {
        this.mActivity = activity;
        this.Eddystone = EddystoneList;
    }

    @Override
    public int getCount() {
        return Eddystone.size();
    }

    @Override
    public Object getItem(int i) {
        return Eddystone.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        TextView tvRSSI, tvTX, tvDistance, tvEddystoneType, tvEddystoneTypeValue, tvEddystoneValue;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(R.layout.adapter_eddystone, viewGroup, false);

            holder = new ViewHolder();
            holder.tvRSSI = (TextView) view.findViewById(R.id.adapter_eddystone_tvRSSI);
            holder.tvTX = (TextView) view.findViewById(R.id.adapter_eddystone_tvTX);
            holder.tvDistance = (TextView) view.findViewById(R.id.adapter_eddystone_tvDistance);
            holder.tvEddystoneType = (TextView) view.findViewById(R.id.adapter_eddystone_tvEddystoneType);
            holder.tvEddystoneTypeValue = (TextView) view.findViewById(R.id.adapter_eddystone_tvEddystoneValueType);
            holder.tvEddystoneValue = (TextView) view.findViewById(R.id.adapter_eddystone_tvEddystoneValue);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Eddystone e = (Eddystone) getItem(i);

        if (e.isEid()){
            holder.tvEddystoneType.setText("Eid");
            holder.tvEddystoneValue.setText(""+e.eid.beaconName+"\n"+ e.eid.description);
        }
        else if (e.isUid()){
            holder.tvEddystoneType.setText("Uid");
        }
        else if (e.isUrl()){
            holder.tvEddystoneType.setText("Url");
            holder.tvEddystoneValue.setText(""+e.url);
        }


        holder.tvRSSI.setText(""+e.rssi);
        holder.tvTX.setText(""+e.calibratedTxPower);

        holder.tvDistance.setText(String.format("%.2f", getDistance(e.rssi, e.calibratedTxPower-41)));



/*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastEddystone(e);
            }
        });
*/
        return view;
    }




    void toastEddystone(Eddystone e){
        Toast.makeText(mActivity, "Battery Voltage: "+ e.telemetry.batteryVoltage+ "\n Tempeture: " + e.telemetry.temperature + "\n Uptime: "+ e.telemetry.uptimeMillis + "\n Packet Counter: " + e.telemetry.packetCounter, Toast.LENGTH_LONG).show();
    }


    double getDistance(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     *
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }


}
