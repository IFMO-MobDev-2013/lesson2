package com.polarnick.polaris.concurrency;

/**
 * Date: 17.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public interface AsyncCallback<Result> {

    public void onSuccess(Result result);

    public void onFailure(Throwable reason);

}
