package com.example.asteroidesreal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScoreAdapter extends BaseAdapter {
    private Context context;
    private List<Score> scores;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;

    public ScoreAdapter(Context context, List<Score> scores) {
        this.context = context;
        this.scores = scores;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return scores.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_score, parent, false);
            holder = new ViewHolder();
            holder.tvPosicion = convertView.findViewById(R.id.tvPosicion);
            holder.tvNombre = convertView.findViewById(R.id.tvNombre);
            holder.tvPuntaje = convertView.findViewById(R.id.tvPuntaje);
            holder.tvFecha = convertView.findViewById(R.id.tvFecha);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Score score = scores.get(position);
        holder.tvPosicion.setText(String.valueOf(position + 1));
        holder.tvNombre.setText(score.getNombre());
        holder.tvPuntaje.setText(String.valueOf(score.getPuntaje()));
        holder.tvFecha.setText(dateFormat.format(new Date(score.getFecha())));
        
        return convertView;
    }
    
    private static class ViewHolder {
        TextView tvPosicion;
        TextView tvNombre;
        TextView tvPuntaje;
        TextView tvFecha;
    }
}













