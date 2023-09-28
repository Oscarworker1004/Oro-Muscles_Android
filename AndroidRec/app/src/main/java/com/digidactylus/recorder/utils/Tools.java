package com.digidactylus.recorder.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.RawRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.MuscleModel;
import com.digidactylus.recorder.models.SetsModel;
import com.digidactylus.recorder.models.TrainingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Tools {

    private static final String TAG = "Tools";
    public static void savePrefValue(Context context, String prefName, ArrayList<Object> value) {
        TinyDB tinyDB = new TinyDB(context);
        tinyDB.putListObject(prefName, value);
    }

    public static void savePrefValue(Context context, String prefName, Object value) {
        TinyDB tinyDB = new TinyDB(context);
        tinyDB.putObject(prefName, value);
    }

    public static void savePrefValue(Context context, String prefName, int value) {
        TinyDB tinyDB = new TinyDB(context);
        tinyDB.putInt(prefName, value);
    }

    public static int getPrefValue(Context context, String prefName) {
        TinyDB tinyDB = new TinyDB(context);
        return tinyDB.getInt(prefName);
    }



    public static List<Object> getPrefValue(Context context, String prefName, Class<?> className) {
        TinyDB tinyDB = new TinyDB(context);
        return tinyDB.getListObject(prefName, className);
    }

    public static String barColorString(double x, double k, int type) {
        double difference = Math.abs(x - k);
        double percentageDifference = (difference / k) * 100;

        if (x > k) {
            if (percentageDifference >= 0 && percentageDifference <= 4) {
                return "#FFFFFF";
            } else if (percentageDifference >= 5 && percentageDifference <= 30) {
                return "#EDCFB6";
            } else if (percentageDifference >= 31 && percentageDifference <= 70) {
                return "#EFB37F";
            } else {
                return "#FA8C2B";
            }
        } else if (x < k) {
            if (percentageDifference >= 0 && percentageDifference <= 4) {
                return "#FFFFFF";
            } else if (percentageDifference >= 5 && percentageDifference <= 30) {
                return "#D9DEE4";
            } else if (percentageDifference >= 31 && percentageDifference <= 70) {
                return "#9EAFBF";
            } else {
                return "#406E9F";
            }
        } else {
            return "#FFFFFF";
        }
    }

    public static List<Double> autoScaleBars(List<Float> barData, int maxBarHeight) {
        // Find the maximum value in the barData list
        double maxValue = 0.0;
        for (Float value : barData) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // Calculate the scaling factor
        double scalingFactor = (maxValue > 0) ? (double) maxBarHeight / maxValue : 1.0;

        // Apply the scaling factor to each value in the list
        List<Double> scaledData = new ArrayList<>();
        for (Float value : barData) {
            scaledData.add(value * scalingFactor);
        }

        return scaledData;
    }

    public static void playMediaPlayer(Context context,@RawRes int file) {
        try {
            MediaPlayer mp = MediaPlayer.create(context, file);
            if (mp == null) return;
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.release();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<TrainingModel> readExercisesJSON(Context context) {
        List<TrainingModel> trainingModels = new ArrayList<>();

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.exercises);
            int size = inputStream.available();

            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(json);
            JSONObject exerciseList = jsonObject.getJSONObject("exerciseList");

            Iterator<String> categoryKeys = exerciseList.keys();
            ArrayList<Object> selectedExercises = getSelectedExercises(context);
            int d = 1000;
            while (categoryKeys.hasNext()) {
                String category = categoryKeys.next();

                TrainingModel trainingModel = new TrainingModel();
                trainingModel.setCategory(category);
                trainingModel.setCustom(false);

                ArrayList<ExerciseModel> exercies = new ArrayList<>();

                JSONArray exercisesArray = exerciseList.getJSONArray(category);
                for (int i = 0; i < exercisesArray.length(); i++) {
                    String id = d + "" + i;
                    ExerciseModel exerciseModel = new ExerciseModel();
                    ExerciseModel existedExercise = getSelectedExerciseIfExists(selectedExercises, exercisesArray.getString(i));
                    if(existedExercise != null) {
                        exerciseModel.setId(existedExercise.getId());
                        exerciseModel.setName(existedExercise.getName());
                        exerciseModel.setSets(existedExercise.getSets());
                        exerciseModel.setWeight(existedExercise.getWeight());
                        exerciseModel.setReps(existedExercise.getReps());
                        exerciseModel.setSetsModels(existedExercise.getSetsModels());
                        exerciseModel.setTonnage(existedExercise.getTonnage());
                        exerciseModel.setSelected(true);
                    }
                    else {
                        exerciseModel.setId(Integer.parseInt(id));
                        exerciseModel.setName(exercisesArray.getString(i));
                        exerciseModel.setSets(1);
                        exerciseModel.setWeight(0);
                        exerciseModel.setReps(1);
                        exerciseModel.setTonnage(0);
                        exerciseModel.setSelected(false);
                    }
                    exercies.add(exerciseModel);
                }
                trainingModel.setExerciseModelList(exercies);
                trainingModels.add(trainingModel);
                d = d + 1000;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return trainingModels;
    }

    public static Map<String, List<MuscleModel>> readMusclesJSON(Context context) {
        Map<String, List<MuscleModel>> muscleMap = new HashMap<>();

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.muscles);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);

            Iterator<String> categoryKeys = jsonObject.keys();
            while (categoryKeys.hasNext()) {
                String category = categoryKeys.next();

                JSONArray musclesArray = jsonObject.getJSONArray(category);
                List<MuscleModel> muscleList = new ArrayList<>();
                for (int i = 0; i < musclesArray.length(); i++) {
                    JSONObject object = musclesArray.getJSONObject(i);
                    String muscleName = object.getString("muscle");
                    String imageName = object.getString("image");
                    int imageResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

                    MuscleModel muscleModel = new MuscleModel(imageResourceId, muscleName, false);
                    muscleList.add(muscleModel);
                }
                muscleMap.put(category, muscleList);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return muscleMap;
    }


    private static ExerciseModel getSelectedExerciseIfExists(List<Object> objectList, String exerciseName) {
        for(Object obj: objectList) {
            ExerciseModel ex = (ExerciseModel) obj;
            if(ex.getName().equals(exerciseName)) {
                return ex;
            }
        }
        return null;
    }

    public static void addToCustomExercise(Context context, String exercise, boolean isSelected) {
        TinyDB tinyDB = new TinyDB(context);
        TrainingModel custom = tinyDB.getObject(Constants.PREF_CUSTOM_EXERCIESES, TrainingModel.class);
        ExerciseModel exerciseModel = new ExerciseModel();
        exerciseModel.setName(exercise);
        exerciseModel.setWeight(0);
        exerciseModel.setReps(1);
        exerciseModel.setSets(1);
        exerciseModel.setTonnage(0);
        exerciseModel.setSelected(isSelected);

        TrainingModel trainingModel;
        if (custom != null) {
            trainingModel = custom;
            exerciseModel.setId(trainingModel.getExerciseModelList().size());
            if(isSelected) {
                changeExerciseState(context, exerciseModel, true);
            }
            trainingModel.getExerciseModelList().add(exerciseModel);
        } else {
            trainingModel = new TrainingModel();
            trainingModel.setCategory("Custom Exercises");
            trainingModel.setCustom(true);
            List<ExerciseModel> exerciseModels = new ArrayList<>();
            exerciseModel.setId(0);
            exerciseModels.add(exerciseModel);
            if(isSelected) {
                changeExerciseState(context, exerciseModel, true);
            }
            trainingModel.setExerciseModelList(exerciseModels);
        }
        tinyDB.putObject(Constants.PREF_CUSTOM_EXERCIESES, trainingModel);
    }

    public static void deleteFromCustomTraining(Context context, ExerciseModel exerciseModel) {
        TrainingModel trainingModel = getCustomExercises(context);
        TinyDB tinyDB = new TinyDB(context);
        List<ExerciseModel> newList = new ArrayList<>();
        if(trainingModel != null) {
            for (ExerciseModel item : trainingModel.getExerciseModelList())  {
                if(!item.getName().equals(exerciseModel.getName())) {
                    newList.add(item);
                }
            }
            trainingModel.setExerciseModelList(newList);
            tinyDB.putObject(Constants.PREF_CUSTOM_EXERCIESES, trainingModel);
        }
    }

    public static TrainingModel getCustomExercises(Context context) {
        TinyDB tinyDB = new TinyDB(context);
        TrainingModel trainingModel = tinyDB.getObject(Constants.PREF_CUSTOM_EXERCIESES, TrainingModel.class);
        ArrayList<Object> selectedExercises = getSelectedExercises(context);
        if (trainingModel != null) {
            for(ExerciseModel exerciseModel : trainingModel.getExerciseModelList()) {
                ExerciseModel existedExercise = getSelectedExerciseIfExists(selectedExercises, exerciseModel.getName());
                if(existedExercise != null) {
                    exerciseModel.setWeight(existedExercise.getWeight());
                    exerciseModel.setSetsModels(existedExercise.getSetsModels());
                    exerciseModel.setSets(existedExercise.getSets());
                    exerciseModel.setReps(existedExercise.getReps());
                    exerciseModel.setId(existedExercise.getId());
                    exerciseModel.setTonnage(existedExercise.getTonnage());
                    exerciseModel.setSelected(true);
                }
                else {
                    exerciseModel.setSelected(false);
                }

            }
            return trainingModel;
        } else {
            return null;
        }
    }

    public static boolean checkCustomExercies(Context context, String txt) {
        TrainingModel trainingModel = Tools.getCustomExercises(context);
        if(trainingModel != null) {
            for (ExerciseModel exerciseModel : trainingModel.getExerciseModelList()) {
                if (exerciseModel.getName().equals(txt)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<Object> getSelectedExercises(Context context) {
        TinyDB tinyDB = new TinyDB(context);
        return tinyDB.getListObject(Constants.PREF_SELECTED_EXERCIESES, ExerciseModel.class);
    }

    public static ExerciseModel getSelectedExerciseByIndex(Context context, String str) {
        List<Object> list = getSelectedExercises(context);
        for(Object obj: list) {
            ExerciseModel item = (ExerciseModel) obj;
            if(item.getName().equalsIgnoreCase(str)) {
                return item;
            }
        }
        return null;
    }

    public static List<String> getSelectedExercisesString(Context context) {
        List<Object> list = getSelectedExercises(context);
        List<String> data = new ArrayList<>();
        for(Object obj: list) {
            ExerciseModel item = (ExerciseModel) obj;
            data.add(item.getName());
        }
        return data;
    }


    public static String shortExercise (String exerciseName) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] words = exerciseName.split(" ");

        for (String word: words) {
            if(!word.isEmpty()) {
                stringBuilder.append(word.charAt(0)).append(".");
            }
        }

        return stringBuilder.toString();
    }
    public static void changeExerciseState(Context context, ExerciseModel exerciseModel, boolean isSelected) {
        TinyDB tinyDB = new TinyDB(context);

        ArrayList<Object> selectedExercises = getSelectedExercises(context);
        ArrayList<Object> modifiedSelected = new ArrayList<>(selectedExercises);
        ExerciseModel existingExercise = null;

        for (Object obj : selectedExercises) {
            ExerciseModel item = (ExerciseModel) obj;
            if (item.getName().equals(exerciseModel.getName())) {
                existingExercise = item;
                break;
            }
        }

        if (existingExercise != null) {
            if (!isSelected) {
                modifiedSelected.remove(existingExercise);
            }
        } else if (isSelected) {
            modifiedSelected.add(exerciseModel);
        }

        tinyDB.putListObject(Constants.PREF_SELECTED_EXERCIESES, modifiedSelected);

    }



    public static int calculateDataInSets(List<SetsModel> setsList, int type) {
        int data = 0;

        for (SetsModel setsModel : setsList) {
            if(type == 0) {
                data += setsModel.getWeight();
            }
            else {
                data += setsModel.getReps();
            }
        }

        return data;
    }

    public static int calculateTonnage(List<SetsModel> setsModels) {
        int data = 0;

        for (SetsModel setsModel : setsModels) {
            data += setsModel.getReps() * setsModel.getWeight();
        }

        return data;
    }


    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        Log.d("klfdjslkfdsjfds", "bitmapToBase64(bitmap)");
        return bitmap;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Fragment getCurrentTopFragment(FragmentManager fm) {
        int stackCount = fm.getBackStackEntryCount();

        if (stackCount > 0) {
            FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(stackCount - 1);
            return fm.findFragmentByTag(backEntry.getName());
        } else {
            List<Fragment> fragments = fm.getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (Fragment f : fragments) {
                    if (f != null && !f.isHidden()) {
                        return f;
                    }
                }
            }
        }
        return null;
    }
}
