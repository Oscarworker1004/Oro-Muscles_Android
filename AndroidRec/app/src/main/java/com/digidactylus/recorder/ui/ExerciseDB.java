/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digidactylus.recorder.ui;

import com.digidactylus.recorder.MainActivity;

/**
 *
 * @author manfred
 */
public class ExerciseDB {
   
    String [] m_top;
    String [][] m_exer;
    private int m_expand = -1;
    
    public ExerciseDB() {
		update();
	}

	public void update() {
        m_top = new String[12];
        
        m_top[0] = "Training Plan";
        m_top[1] = "Chest Exercises";
        m_top[2] = "Shoulder Exercises";
        m_top[3] = "Bicep Exercises";
        m_top[4] = "Triceps Exercises";
        m_top[5] = "Leg Exercises";
        m_top[6] = "Back Exercises";
        m_top[7] = "Glute Exercises";
        m_top[8] = "Ab Exercises";
        m_top[9] = "Calves Exercises";
        m_top[10] = "Forearm Flexors & Grip Exercises";
        m_top[11] = "Forearm Extensor Exercises";

        m_exer = new String[m_top.length][];
        
        
        m_exer[0] = new String[1];


		String cust = MainActivity.getMain().getPref("exercises");
		String [] custEx = cust.split(";");
		if (custEx != null) {
			m_exer[0] = new String[1 + custEx.length];
			for (int j = 0; j < custEx.length; j++)
				m_exer[0][j + 1] = custEx[j];
		}

        m_exer[1] = new String[30];
        m_exer[2] = new String[35];
        m_exer[3] = new String[13];
        m_exer[4] = new String[11];
        m_exer[5] = new String[31];
        m_exer[6] = new String[44];
        m_exer[7] = new String[22];
        m_exer[8] = new String[22];
        m_exer[9] = new String[5];
        m_exer[10] = new String[12];
        m_exer[11] = new String[3];

   		m_exer[0][0] = "Training Plan";

		m_exer[1][0] = "Chest Exercises";
		m_exer[1][1] = "Bar Dip";
		m_exer[1][2] = "Bench Press";
		m_exer[1][3] = "Cable Chest Press";
		m_exer[1][4] = "Close-Grip Bench Press";
		m_exer[1][5] = "Close-Grip Feet-Up Bench Press";
		m_exer[1][6] = "Decline Bench Press";
		m_exer[1][7] = "Dumbbell Chest Fly";
		m_exer[1][8] = "Dumbbell Chest Press";
		m_exer[1][9] = "Dumbbell Decline Chest Press";
		m_exer[1][10] = "Dumbbell Floor Press";
		m_exer[1][11] = "Dumbbell Pullover";
		m_exer[1][12] = "Feet-Up Bench Press";
		m_exer[1][13] = "Floor Press";
		m_exer[1][14] = "Incline Bench Press";
		m_exer[1][15] = "Incline Dumbbell Press";
		m_exer[1][16] = "Incline Push-Up";
		m_exer[1][17] = "Kneeling Incline Push-Up";
		m_exer[1][18] = "Kneeling Push-Up";
		m_exer[1][19] = "Machine Chest Fly";
		m_exer[1][20] = "Machine Chest Press";
		m_exer[1][21] = "Pec Deck";
		m_exer[1][22] = "Push-Up";
		m_exer[1][23] = "Push-Up Against Wall";
		m_exer[1][24] = "Push-Ups With Feet in Rings";
		m_exer[1][25] = "Resistance Band Chest Fly";
		m_exer[1][26] = "Smith Machine Bench Press";
		m_exer[1][27] = "Smith Machine Incline Bench Press";
		m_exer[1][28] = "Standing Cable Chest Fly";
		m_exer[1][29] = "Standing Resistance Band Chest Fly";

		m_exer[2][0] = "Shoulder Exercises";
		m_exer[2][1] = "Band External Shoulder Rotation";
		m_exer[2][2] = "Band Internal Shoulder Rotation";
		m_exer[2][3] = "Band Pull-Apart";
		m_exer[2][4] = "Barbell Front Raise";
		m_exer[2][5] = "Barbell Rear Delt Row";
		m_exer[2][6] = "Barbell Upright Row";
		m_exer[2][7] = "Behind the Neck Press";
		m_exer[2][8] = "Cable Lateral Raise";
		m_exer[2][9] = "Cable Rear Delt Row";
		m_exer[2][10] = "Dumbbell Front Raise";
		m_exer[2][11] = "Dumbbell Horizontal Internal Shoulder Rotation";
		m_exer[2][12] = "Dumbbell Horizontal External Shoulder Rotation";
		m_exer[2][13] = "Dumbbell Lateral Raise";
		m_exer[2][14] = "Dumbbell Rear Delt Row";
		m_exer[2][15] = "Dumbbell Shoulder Press";
		m_exer[2][16] = "Face Pull";
		m_exer[2][17] = "Front Hold";
		m_exer[2][18] = "Lying Dumbbell External Shoulder Rotation";
		m_exer[2][19] = "Lying Dumbbell Internal Shoulder Rotation";
		m_exer[2][20] = "Machine Lateral Raise";
		m_exer[2][21] = "Machine Shoulder Press";
		m_exer[2][22] = "Monkey Row";
		m_exer[2][23] = "Overhead Press";
		m_exer[2][24] = "Plate Front Raise";
		m_exer[2][25] = "Power Jerk";
		m_exer[2][26] = "Push Press";
		m_exer[2][27] = "Reverse Dumbbell Flyes";
		m_exer[2][28] = "Reverse Machine Fly";
		m_exer[2][29] = "Seated Dumbbell Shoulder Press";
		m_exer[2][30] = "Seated Barbell Overhead Press";
		m_exer[2][31] = "Seated Smith Machine Shoulder Press";
		m_exer[2][32] = "Snatch Grip Behind the Neck Press";
		m_exer[2][33] = "Squat Jerk";
		m_exer[2][34] = "Split Jerk";

		m_exer[3][0] = "Bicep Exercises";
		m_exer[3][1] = "Barbell Curl";
		m_exer[3][2] = "Barbell Preacher Curl";
		m_exer[3][3] = "Bodyweight Curl";
		m_exer[3][4] = "Cable Curl With Bar";
		m_exer[3][5] = "Cable Curl With Rope";
		m_exer[3][6] = "Concentration Curl";
		m_exer[3][7] = "Dumbbell Curl";
		m_exer[3][8] = "Dumbbell Preacher Curl";
		m_exer[3][9] = "Hammer Curl";
		m_exer[3][10] = "Incline Dumbbell Curl";
		m_exer[3][11] = "Machine Bicep Curl";
		m_exer[3][12] = "Spider Curl";

		m_exer[4][0] = "Triceps Exercises";
		m_exer[4][1] = "Barbell Standing Triceps Extension";
		m_exer[4][2] = "Barbell Lying Triceps Extension";
		m_exer[4][3] = "Bench Dip";
		m_exer[4][4] = "Close-Grip Push-Up";
		m_exer[4][5] = "Dumbbell Lying Triceps Extension";
		m_exer[4][6] = "Dumbbell Standing Triceps Extension";
		m_exer[4][7] = "Overhead Cable Triceps Extension";
		m_exer[4][8] = "Tricep Bodyweight Extension";
		m_exer[4][9] = "Tricep Pushdown With Bar";
		m_exer[4][10] = "Tricep Pushdown With Rope";

		m_exer[5][0] = "Leg Exercises";
		m_exer[5][1] = "Air Squat";
		m_exer[5][2] = "Barbell Hack Squat";
		m_exer[5][3] = "Barbell Lunge";
		m_exer[5][4] = "Barbell Walking Lunge";
		m_exer[5][5] = "Belt Squat";
		m_exer[5][6] = "Body Weight Lunge";
		m_exer[5][7] = "Box Squat";
		m_exer[5][8] = "Bulgarian Split Squat";
		m_exer[5][9] = "Chair Squat";
		m_exer[5][10] = "Dumbbell Lunge";
		m_exer[5][11] = "Dumbbell Squat";
		m_exer[5][12] = "Front Squat";
		m_exer[5][13] = "Goblet Squat";
		m_exer[5][14] = "Hack Squat Machine";
		m_exer[5][15] = "Half Air Squat";
		m_exer[5][16] = "Hip Adduction Machine";
		m_exer[5][17] = "Landmine Hack Squat";
		m_exer[5][18] = "Landmine Squat";
		m_exer[5][19] = "Leg Extension";
		m_exer[5][20] = "Leg Press";
		m_exer[5][21] = "Lying Leg Curl";
		m_exer[5][22] = "Pause Squat";
		m_exer[5][23] = "Romanian Deadlift";
		m_exer[5][24] = "Safety Bar Squat";
		m_exer[5][25] = "Seated Leg Curl";
		m_exer[5][26] = "Shallow Body Weight Lunge";
		m_exer[5][27] = "Side Lunges (Bodyweight)";
		m_exer[5][28] = "Smith Machine Squat";
		m_exer[5][29] = "Squat";
		m_exer[5][30] = "Step Up";

		m_exer[6][0] = "Back Exercises";
		m_exer[6][1] = "Back Extension";
		m_exer[6][2] = "Barbell Row";
		m_exer[6][3] = "Barbell Shrug";
		m_exer[6][4] = "Block Snatch";
		m_exer[6][5] = "Cable Close Grip Seated Row";
		m_exer[6][6] = "Cable Wide Grip Seated Row";
		m_exer[6][7] = "Chin-Up";
		m_exer[6][8] = "Clean";
		m_exer[6][9] = "Clean and Jerk";
		m_exer[6][10] = "Deadlift";
		m_exer[6][11] = "Deficit Deadlift";
		m_exer[6][12] = "Dumbbell Deadlift";
		m_exer[6][13] = "Dumbbell Row";
		m_exer[6][14] = "Dumbbell Shrug";
		m_exer[6][15] = "Floor Back Extension";
		m_exer[6][16] = "Good Morning";
		m_exer[6][17] = "Hang Clean";
		m_exer[6][18] = "Hang Power Clean";
		m_exer[6][19] = "Hang Power Snatch";
		m_exer[6][20] = "Hang Snatch";
		m_exer[6][21] = "Inverted Row";
		m_exer[6][22] = "Inverted Row with Underhand Grip";
		m_exer[6][23] = "Kettlebell Swing";
		m_exer[6][24] = "Lat Pulldown With Pronated Grip";
		m_exer[6][25] = "Lat Pulldown With Supinated Grip";
		m_exer[6][26] = "One-Handed Cable Row";
		m_exer[6][27] = "One-Handed Lat Pulldown";
		m_exer[6][28] = "Pause Deadlift";
		m_exer[6][29] = "Pendlay Row";
		m_exer[6][30] = "Power Clean";
		m_exer[6][31] = "Power Snatch";
		m_exer[6][32] = "Pull-Up";
		m_exer[6][33] = "Rack Pull";
		m_exer[6][34] = "Seal Row";
		m_exer[6][35] = "Seated Machine Row";
		m_exer[6][36] = "Snatch";
		m_exer[6][37] = "Snatch Grip Deadlift";
		m_exer[6][38] = "Stiff-Legged Deadlift";
		m_exer[6][39] = "Straight Arm Lat Pulldown";
		m_exer[6][40] = "Sumo Deadlift";
		m_exer[6][41] = "T-Bar Row";
		m_exer[6][42] = "Trap Bar Deadlift With High Handles";
		m_exer[6][43] = "Trap Bar Deadlift With Low Handles";

		m_exer[7][0] = "Glute Exercises";
		m_exer[7][1] = "Banded Side Kicks";
		m_exer[7][2] = "Cable Pull Through";
		m_exer[7][3] = "Clamshells";
		m_exer[7][4] = "Dumbbell Romanian Deadlift";
		m_exer[7][5] = "Dumbbell Frog Pumps";
		m_exer[7][6] = "Fire Hydrants";
		m_exer[7][7] = "Frog Pumps";
		m_exer[7][8] = "Glute Bridge";
		m_exer[7][9] = "Hip Abduction Against Band";
		m_exer[7][10] = "Hip Abduction Machine";
		m_exer[7][11] = "Hip Thrust";
		m_exer[7][12] = "Hip Thrust Machine";
		m_exer[7][13] = "Hip Thrust With Band Around Knees";
		m_exer[7][14] = "Lateral Walk With Band";
		m_exer[7][15] = "Machine Glute Kickbacks";
		m_exer[7][16] = "One-Legged Glute Bridge";
		m_exer[7][17] = "One-Legged Hip Thrust";
		m_exer[7][18] = "Romanian Deadlift";
		m_exer[7][19] = "Single Leg Romanian Deadlift";
		m_exer[7][20] = "Standing Glute Kickback in Machine";
		m_exer[7][21] = "Step Up";

		m_exer[8][0] = "Ab Exercises";
		m_exer[8][1] = "Cable Crunch";
		m_exer[8][2] = "Crunch";
		m_exer[8][3] = "Dead Bug";
		m_exer[8][4] = "Hanging Leg Raise";
		m_exer[8][5] = "Hanging Knee Raise";
		m_exer[8][6] = "Hanging Sit-Up";
		m_exer[8][7] = "High to Low Wood Chop with Band";
		m_exer[8][8] = "Horizontal Wood Chop with Band";
		m_exer[8][9] = "Kneeling Ab Wheel Roll-Out";
		m_exer[8][10] = "Kneeling Plank";
		m_exer[8][11] = "Kneeling Side Plank";
		m_exer[8][12] = "Lying Leg Raise";
		m_exer[8][13] = "Lying Windshield Wiper";
		m_exer[8][14] = "Lying Windshield Wiper with Bent Knees";
		m_exer[8][15] = "Machine Crunch";
		m_exer[8][16] = "Mountain Climbers";
		m_exer[8][17] = "Oblique Crunch";
		m_exer[8][18] = "Oblique Sit-Up";
		m_exer[8][19] = "Plank";
		m_exer[8][20] = "Side Plank";
		m_exer[8][21] = "Sit-Up";

		m_exer[9][0] = "Calves Exercises";
		m_exer[9][1] = "Eccentric Heel Drop";
		m_exer[9][2] = "Heel Raise";
		m_exer[9][3] = "Seated Calf Raise";
		m_exer[9][4] = "Standing Calf Raise";

		m_exer[10][0] = "Forearm Flexors & Grip Exercises";
		m_exer[10][1] = "Barbell Wrist Curl";
		m_exer[10][2] = "Barbell Wrist Curl Behind the Back";
		m_exer[10][3] = "Bar Hang";
		m_exer[10][4] = "Dumbbell Wrist Curl";
		m_exer[10][5] = "Farmers Walk";
		m_exer[10][6] = "Fat Bar Deadlift";
		m_exer[10][7] = "Gripper";
		m_exer[10][8] = "One-Handed Bar Hang";
		m_exer[10][9] = "Plate Pinch";
		m_exer[10][10] = "Plate Wrist Curl";
		m_exer[10][11] = "Towel Pull-Up";

		m_exer[11][0] = "Forearm Extensor Exercises";
		m_exer[11][1] = "Barbell Wrist Extension";
		m_exer[11][2] = "Dumbbell Wrist Extension";
     
        
        
        
    }
    
    public void expand(int n) {
        m_expand = n;
    }

    public int getExpand() {
        return m_expand;
    }
    
    public String [] getList() {
        if (m_expand == -1)
            return getTopLevel();
        return m_exer[m_expand];
    }
    
    public String [] getTopLevel() {
        return m_top;
    }
}
