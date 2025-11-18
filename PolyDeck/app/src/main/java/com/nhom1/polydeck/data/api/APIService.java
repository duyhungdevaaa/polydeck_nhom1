package com.nhom1.polydeck.data.api;

import com.nhom1.polydeck.data.model.AdminStats;
import com.nhom1.polydeck.data.model.BoTu;
import com.nhom1.polydeck.data.model.TuVung;
import com.nhom1.polydeck.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface APIService {

    // ============= ADMIN DASHBOARD =============
    @GET("admin/stats")
    Call<AdminStats> getAdminStats();

    // ============= QUẢN LÝ NGƯỜI DÙNG =============
    @GET("admin/users")
    Call<List<User>> getAllUsers();

    @GET("admin/users/search")
    Call<List<User>> searchUsers(@Query("query") String query);

    @GET("admin/users/{id}")
    Call<User> getUserDetail(@Path("id") String userId);

    @PUT("admin/users/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User user);

    @POST("admin/users/{id}/block")
    Call<Void> blockUser(@Path("id") String userId);

    @POST("admin/users/{id}/unblock")
    Call<Void> unblockUser(@Path("id") String userId);

    // ============= QUẢN LÝ BỘ TỪ =============
    @GET("admin/botu")
    Call<List<BoTu>> getAllBoTu();

    @GET("admin/botu/search")
    Call<List<BoTu>> searchBoTu(@Query("query") String query);

    @GET("admin/botu/{id}")
    Call<BoTu> getBoTuDetail(@Path("id") String boTuId);

    @POST("admin/botu")
    Call<BoTu> createBoTu(@Body BoTu boTu);

    @PUT("admin/botu/{id}")
    Call<BoTu> updateBoTu(@Path("id") String boTuId, @Body BoTu boTu);

    @DELETE("admin/botu/{id}")
    Call<Void> deleteBoTu(@Path("id") String boTuId);

    // ============= QUẢN LÝ TỪ VỰNG =============
    @GET("admin/botu/{id}/tuvung")
    Call<List<TuVung>> getTuVungByBoTu(@Path("id") String boTuId);

    @POST("admin/tuvung")
    Call<TuVung> addTuVung(@Body TuVung tuVung);

    @PUT("admin/tuvung/{id}")
    Call<TuVung> updateTuVung(@Path("id") String tuVungId, @Body TuVung tuVung);

    @DELETE("admin/tuvung/{id}")
    Call<Void> deleteTuVung(@Path("id") String tuVungId);

    @POST("admin/tuvung/import")
    Call<Void> importTuVungFromExcel(
            @Query("boTuId") String boTuId,
            @Body List<TuVung> danhSachTuVung
    );
}