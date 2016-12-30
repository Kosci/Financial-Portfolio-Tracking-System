package trunk.Model;

import javafx.stage.Stage;
import trunk.View.CAView;
import trunk.View.EquitiesView;
import trunk.View.FPTS;

/**
 * Created by sadaf345 on 4/12/2016.
 */
public interface handleCommand {
    public void handleCommand(final Stage primaryStage, FPTS fpts, EquitiesView equitiesView, CAView caView);
}
