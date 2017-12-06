package nhan1303.watsong.interfaceWatSong;

import nhan1303.watsong.model.InfoSong;

/**
 * Created by NHAN on 14/11/2017.
 */

public interface CommunicationInterface {
    void onClickFragmentMain(InfoSong infoSong);
    void updateListSongAfterRemove(InfoSong infoSong);
    void idUser(String id);
}
