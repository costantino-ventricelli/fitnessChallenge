package it.fitnesschallenge.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.NumberFormat;
import java.util.Locale;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.FloatToDateFormatter;

@SuppressLint("ViewConstructor")
public class LineChartMakerView extends MarkerView {

    private TextView mDate;
    private TextView mWeight;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context        indica il contesto in cui deve essere visualizzato il box
     * @param layoutResource la risorsa del layout per MakerView.
     */
    public LineChartMakerView(Context context, int layoutResource) {
        super(context, layoutResource);

        mDate = findViewById(R.id.floating_box_date);
        mWeight = findViewById(R.id.floating_box_weight);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        FloatToDateFormatter floatToDateFormatter = new FloatToDateFormatter(e.getX());
        mDate.setText(floatToDateFormatter.formatDate());
        String weight = NumberFormat.getInstance(Locale.getDefault()).format(e.getY()) + "Kg";
        mWeight.setText(weight);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
