package ni.edu.uam.psyconnect.network

import ni.edu.uam.psyconnect.data.model.Achievement
import ni.edu.uam.psyconnect.data.model.AuthResponse
import ni.edu.uam.psyconnect.data.model.ChangeEmailRequest
import ni.edu.uam.psyconnect.data.model.ChangePasswordRequest
import ni.edu.uam.psyconnect.data.model.LoginRequest
import ni.edu.uam.psyconnect.data.model.Psychologist
import ni.edu.uam.psyconnect.data.model.Mood
import ni.edu.uam.psyconnect.data.model.RecoveryCodeRequest
import ni.edu.uam.psyconnect.data.model.ResetPasswordRequest
import ni.edu.uam.psyconnect.data.model.TestResult
import ni.edu.uam.psyconnect.data.model.User
import ni.edu.uam.psyconnect.data.model.VerifyCodeRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/users/register")
    suspend fun registerUser(@Body user: User): Response<User>

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: User): Response<User>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @POST("api/results")
    suspend fun saveResult(@Body result: TestResult): Response<TestResult>

    @GET("api/results/{userId}")
    suspend fun getHistory(@Path("userId") userId: Long): Response<List<TestResult>>

    @GET("api/psychologists")
    suspend fun getPsychologists(): Response<List<Psychologist>>

    // Este endpoint funciona para enviar códigos (tanto registro como recuperación)
    @POST("api/verification/send")
    suspend fun sendVerificationCode(@Query("email") email: String): Response<ResponseBody>

    // Usaremos este endpoint estándar para validar cualquier código
    @POST("api/verification/validate")
    suspend fun validateCode(@Body request: VerifyCodeRequest): Response<Boolean>

    @POST("api/users/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>

    // Mantenemos estos por si el backend los soporta, pero preferimos los de arriba
    @POST("api/verification/validate-recovery")
    suspend fun validateRecoveryCode(@Body request: RecoveryCodeRequest): Response<Boolean>

    @GET("api/users/exists-email/{email}")
    suspend fun existsEmail(@Path("email") email: String): Response<Boolean>

    @GET("api/users/exists-username/{username}")
    suspend fun existsUsername(@Path("username") username: String): Response<Boolean>

    @POST("api/users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ResponseBody>

    @POST("api/users/change-email")
    suspend fun changeEmail(@Body request: ChangeEmailRequest): Response<ResponseBody>

    @POST("api/moods")
    suspend fun saveMood(@Body mood: ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry): Response<ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry>

    @GET("api/moods/today/{userId}")
    suspend fun hasMoodToday(@Path("userId") userId: Long): Response<Boolean>

    @GET("api/moods/user/{userId}")
    suspend fun getMoodHistory(@Path("userId") userId: Long): Response<List<ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry>>

    @DELETE("api/moods/{id}")
    suspend fun deleteMood(@Path("id") id: Long): Response<ResponseBody>

    @GET("api/achievements/{userId}")
    suspend fun getAchievements(@Path("userId") userId: Long): Response<List<Achievement>>

}
