package it.fitnesschallenge.adapter;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import it.fitnesschallenge.R;

public class LineChartMakerView extends MarkerView {

    private TextView mDate;
    private TextView mWeight;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context        indica il contesto in cui deve essere visualizzato il box
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public LineChartMakerView(Context context, int layoutResource) {
        super(context, layoutResource);

        mDate = findViewById(R.id.floating_box_date);
        mWeight = findViewById(R.id.floating_box_weight);
    }

    //TODO:ricerare la classe di conversiobe float to date yyyy.dayOfYear.

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
    }
}
