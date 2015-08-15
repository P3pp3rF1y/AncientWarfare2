package net.shadowmage.ancientwarfare.apiimpl;


import net.shadowmage.ancientwarfare.api.API;
import net.shadowmage.ancientwarfare.api.APIBase;
import net.shadowmage.ancientwarfare.api.APIStatus;
import net.shadowmage.ancientwarfare.apiimpl.v1.APIimplv1;

public class APISelector implements APIBase {

    private APISelector() {
    }

    public static void init() {
        API.setAPI(new APISelector());
    }

    @Override
    public APIBase getAPI(int maxVersion) {
        if (maxVersion <= 0) {
            return this;
        } else {
            return new APIimplv1(1, APIStatus.OK);
        }
    }

    @Override
    public APIStatus getStatus() {
        return APIStatus.ERROR;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}