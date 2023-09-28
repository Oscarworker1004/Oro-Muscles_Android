package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.SetsModel;
import com.digidactylus.recorder.utils.AppManager;
import com.digidactylus.recorder.utils.MinValueInputFilter;
import com.digidactylus.recorder.viewmodels.TrainingViewModel;

import java.util.List;
import java.util.Locale;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.SetsViewHolder> {

    interface AdapterListener {
        void onDataChanged(List<SetsModel> setsList);
    }

    private List<SetsModel> setsList;

    final AdapterListener adapterListener;


    private final Context context;

    private boolean repsDataTyped = false;
    private boolean weightDataTyped = false;

    private final String parent;

    private static final String TAG = "SetsAdapter";


    public SetsAdapter(Context context, String parent, List<SetsModel> setsList, AdapterListener adapterListener) {
        this.setsList = setsList;
        this.adapterListener = adapterListener;
        this.parent = parent;
        this.context = context;
    }

    public void setList(List<SetsModel> setsList) {
        this.setsList = setsList;
    }

    public List<SetsModel> getSetsList() {
        return setsList;
    }

    @NonNull
    @Override
    public SetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_set_view, parent, false);
        return new SetsViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return setsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull SetsViewHolder holder, int position) {
        SetsModel setsModel = setsList.get(position);


        InputFilter minValueFilter = new MinValueInputFilter(1);
        InputFilter minValueFilterZero = new MinValueInputFilter(0);
        holder.repsNum.setFilters(new InputFilter[]{minValueFilter});
        holder.weightNum.setFilters(new InputFilter[]{minValueFilterZero});


        holder.setTxt.setText(String.format(Locale.getDefault(), "Set %d", position + 1));
        holder.repsNum.setText(String.valueOf(setsModel.getReps()));
        holder.repsNum.setTag(position);
        holder.weightNum.setText(String.valueOf(setsModel.getWeight()));
        holder.weightNum.setTag(position);

        holder.repsNum.setEnabled(true);
        holder.weightNum.setEnabled(true);


        holder.repsNum.removeTextChangedListener(holder.repsWatcher);
        holder.repsWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (!s.toString().isEmpty()) {
                        int val = Integer.parseInt(s.toString());
                        if (val > 0) {
                            int currentGroupPosition = (int) holder.repsNum.getTag();
                            SetsModel newSet = setsList.get(currentGroupPosition);
                            if(newSet.getReps() != val) {
                                newSet.setReps(val);
                                setsList.set(currentGroupPosition, newSet);
                                repsDataTyped = true;
                                MainActivity.getMain().trainingViewModel.setTrainingModified(true);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        };
        holder.repsNum.addTextChangedListener(holder.repsWatcher);
        holder.repsNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && repsDataTyped) {
                    repsDataTyped = false;

                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                            adapterListener.onDataChanged(setsList);
                            hideKeyboard(v);
                        }
                    });

                }
            }
        });

        holder.weightNum.removeTextChangedListener(holder.weightWatcher);
        holder.weightWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        int val = Integer.parseInt(s.toString());
                        int currentGroupPosition = (int) holder.weightNum.getTag();
                        SetsModel newSet = setsList.get(currentGroupPosition);
                        if(newSet.getWeight() != val) {
                            newSet.setWeight(val);
                            setsList.set(currentGroupPosition, newSet);
                            weightDataTyped = true;
                            MainActivity.getMain().trainingViewModel.setTrainingModified(true);
                        }

                    }
                } catch (Exception ignored) {
                }
            }
        };
        holder.weightNum.addTextChangedListener(holder.weightWatcher);
        holder.weightNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && weightDataTyped) {
                    weightDataTyped = false;
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                            adapterListener.onDataChanged(setsList);
                            hideKeyboard(v);
                        }
                    });

                }
            }
        });

    }

    private void hideKeyboard(android.view.View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void makeChanges() {
        adapterListener.onDataChanged(setsList);
    }

    public static class SetsViewHolder extends RecyclerView.ViewHolder {

        private TextView setTxt;
        private EditText repsNum;
        private EditText weightNum;

        TextWatcher weightWatcher, repsWatcher;

        public SetsViewHolder(View itemView) {
            super(itemView);
            setTxt = itemView.findViewById(R.id.setTxt);
            repsNum = itemView.findViewById(R.id.repsNum);
            weightNum = itemView.findViewById(R.id.weightNum);
        }

    }

}



