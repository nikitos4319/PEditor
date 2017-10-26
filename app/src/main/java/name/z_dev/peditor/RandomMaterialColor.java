package name.z_dev.peditor;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by D.Krylov on 03.08.2015.
 */
public class RandomMaterialColor {

    private static final Random RANDOM = new Random();

    public final static int RED[] = {
            Color.parseColor("#ff8a80"),
            Color.parseColor("#ff5252"),
            Color.parseColor("#ff1744"),
            Color.parseColor("#d50000")
    };

    public final static int PINK[] = {
            Color.parseColor("#ff80ab"),
            Color.parseColor("#ff4081"),
            Color.parseColor("#f50057"),
            Color.parseColor("#c51162")
    };

    public final static int PURPLE[] = {
            Color.parseColor("#ea80fc"),
            Color.parseColor("#e040fb"),
            Color.parseColor("#d500f9"),
            Color.parseColor("#aa00ff")
    };

    public final static int DEEPPURPLE[] = {
            Color.parseColor("#b388ff"),
            Color.parseColor("#7c4dff"),
            Color.parseColor("#651fff"),
            Color.parseColor("#6200ea")
    };

    public final static int INDIGO[] = {
            Color.parseColor("#8c9eff"),
            Color.parseColor("#536dfe"),
            Color.parseColor("#3d5afe"),
            Color.parseColor("#304ffe")
    };

    public final static int BLUE[] = {
            Color.parseColor("#82b1ff"),
            Color.parseColor("#448aff"),
            Color.parseColor("#2979ff"),
            Color.parseColor("#2962ff")
    };

    public final static int LIGHTBLUE[] = {
            Color.parseColor("#80d8ff"),
            Color.parseColor("#40c4ff"),
            Color.parseColor("#00b0ff"),
            Color.parseColor("#0091ea")
    };

    public final static int CYAN[] = {
            Color.parseColor("#84ffff"),
            Color.parseColor("#18ffff"),
            Color.parseColor("#00e5ff"),
            Color.parseColor("#00b8d4")
    };

    public final static int TEAL[] = {
            Color.parseColor("#a7ffeb"),
            Color.parseColor("#64ffda"),
            Color.parseColor("#1de9b6"),
            Color.parseColor("#00bfa5")
    };

    public final static int GREEN[] = {
            Color.parseColor("#b9f6ca"),
            Color.parseColor("#69f0ae"),
            Color.parseColor("#00e676"),
            Color.parseColor("#00c853")
    };

    public final static int LIGHTGREEN[] = {
            Color.parseColor("#ccff90"),
            Color.parseColor("#b2ff59"),
            Color.parseColor("#76ff03"),
            Color.parseColor("#64dd17")
    };

    public final static int LIME[] = {
            Color.parseColor("#f4ff81"),
            Color.parseColor("#eeff41"),
            Color.parseColor("#c6ff00"),
            Color.parseColor("#aeea00")
    };

    public final static int YELLOW[] = {
            Color.parseColor("#ffff8d"),
            Color.parseColor("#ffff00"),
            Color.parseColor("#ffea00"),
            Color.parseColor("#ffd600")
    };

    public final static int AMBER[] = {
            Color.parseColor("#ffe57f"),
            Color.parseColor("#ffd740"),
            Color.parseColor("#ffc400"),
            Color.parseColor("#ffab00")
    };

    public final static int ORANGE[] = {
            Color.parseColor("#ffd180"),
            Color.parseColor("#ffab40"),
            Color.parseColor("#ff9100"),
            Color.parseColor("#ff6d00")
    };

    public final static int DEEPORANGE[] = {
            Color.parseColor("#ff9e80"),
            Color.parseColor("#ff6e40"),
            Color.parseColor("#ff3d00"),
            Color.parseColor("#dd2c00")
    };

    public final static int BROWN[] = {
            Color.parseColor("#d7ccc8"),
            Color.parseColor("#bcaaa4"),
            Color.parseColor("#8d6e63"),
            Color.parseColor("#5d4037")
    };

    public final static int GREY[] = {
            Color.parseColor("#f5f5f5"),
            Color.parseColor("#eeeeee"),
            Color.parseColor("#bdbdbd"),
            Color.parseColor("#616161")
    };

    public final static int BLUEGREY[] = {
            Color.parseColor("#cfd8dc"),
            Color.parseColor("#b0bec5"),
            Color.parseColor("#78909c"),
            Color.parseColor("#455a64")
    };


    public static int getRandomMColor() {
        int subColor = RANDOM.nextInt(1)+2;
        return colorByNum(RANDOM.nextInt(18),subColor);
    }

    public static int getNotRandomMColor(int position) {
        int p = (position+1) % 19;
        int second = position % 38;
        second /= 19;
        second = 3 - second;
        return colorByNum(p, second);
    }

    private static int colorByNum(int color, int subColor){
        switch (color) {
            default:
            case 0:
                return RED[subColor];
            case 1:
                return PINK[subColor];
            case 2:
                return PURPLE[subColor];
            case 3:
                return DEEPPURPLE[subColor];
            case 4:
                return INDIGO[subColor];
            case 5:
                return BLUE[subColor];
            case 6:
                return LIGHTBLUE[subColor];
            case 7:
                return CYAN[subColor];
            case 8:
                return TEAL[subColor];
            case 9:
                return GREEN[subColor];
            case 10:
                return LIGHTGREEN[subColor];
            case 11:
                return LIME[subColor];
            case 12:
                return YELLOW[subColor];
            case 13:
                return AMBER[subColor];
            case 14:
                return ORANGE[subColor];
            case 15:
                return DEEPORANGE[subColor];
            case 16:
                return BROWN[subColor];
            case 17:
                return GREY[subColor];
            case 18:
                return BLUEGREY[subColor];
        }
    }

}