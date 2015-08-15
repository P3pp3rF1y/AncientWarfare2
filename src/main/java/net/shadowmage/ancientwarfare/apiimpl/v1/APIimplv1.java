package net.shadowmage.ancientwarfare.apiimpl.v1;

import net.shadowmage.ancientwarfare.api.API;
import net.shadowmage.ancientwarfare.api.APIBase;
import net.shadowmage.ancientwarfare.api.APIStatus;
import net.shadowmage.ancientwarfare.api.v1.APIv1;

public class APIimplv1 implements APIv1 {
    private final int version;
    private final APIStatus status;

    public APIimplv1(int version, APIStatus status) {
        this.version = version;
        this.status = status;
    }

    @Override
    public APIBase getAPI(int maxVersion) {
        if (maxVersion == version && status == APIStatus.OK) {
            return this;
        } else {
            return API.getAPI(maxVersion);
        }
    }

    @Override
    public APIStatus getStatus() {
        return status;
    }

    @Override
    public int getVersion() {
        return version;
    }
}
