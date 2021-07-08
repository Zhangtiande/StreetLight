package com.app.streetlight.data;

import com.app.streetlight.data.model.LoggedInUser;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private static LoggedInUser user;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public Result<LoggedInUser> login(String username, String password) {

        try {
            LoginThread task = new LoginThread(username, password);
            Future<LoggedInUser> future = executorService.submit(task);
            user = future.get();
            if (user.getUserId().equals("Error!")) {
                return new Result.Error(user.getDisplayName());
            } else {
                return new Result.Success<>(user);
            }
        } catch (Exception e) {
            return new Result.Error(e.toString());
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    @SuppressWarnings("ConstantConditions")
    static class LoginThread implements Callable<LoggedInUser> {
        private final OkHttpClient client;
        private final Request request;

        public LoginThread(String username, String password) {
            super();
            client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build();
            request = new Request.Builder()
                    .url("http://www.923yyds.top/login.api") //目的网址
                    .post(formBody)
                    .build();
        }

        @Override
        public LoggedInUser call() {
            Response response;
            LoggedInUser u = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    if (!resp.contains("{")) {

                        return new LoggedInUser("Error!", resp);
                    }
                    String name = resp.substring(resp.indexOf("=") + 1, resp.length() - 1);
                    u = new LoggedInUser(java.util.UUID.randomUUID().toString(), name);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return u;
        }
    }
}