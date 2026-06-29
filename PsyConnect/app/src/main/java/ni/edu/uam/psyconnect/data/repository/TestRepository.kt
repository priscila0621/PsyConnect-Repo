package ni.edu.uam.psyconnect.data.repository

import ni.edu.uam.psyconnect.data.model.Question

object TestRepository {

    private val defaultOptions =
        listOf(
            "Siempre",
            "Frecuentemente",
            "A veces",
            "Rara vez",
            "Nunca"
        )

    fun getQuestions(
        category: String
    ): List<Question> {

        return when (category) {

            "WELLNESS" -> listOf(

                Question(
                    "Me siento satisfecho con mi vida.",
                    defaultOptions
                ),

                Question(
                    "Disfruto las actividades que realizo diariamente.",
                    defaultOptions
                ),

                Question(
                    "Me siento emocionalmente equilibrado.",
                    defaultOptions
                ),

                Question(
                    "Tengo energía para afrontar mis responsabilidades.",
                    defaultOptions
                ),

                Question(
                    "Me siento motivado para alcanzar mis metas.",
                    defaultOptions
                ),

                Question(
                    "Puedo disfrutar los momentos positivos de mi día.",
                    defaultOptions
                ),

                Question(
                    "Me siento en paz conmigo mismo.",
                    defaultOptions
                ),

                Question(
                    "Mantengo hábitos que favorecen mi bienestar.",
                    defaultOptions
                ),

                Question(
                    "Tengo esperanza respecto a mi futuro.",
                    defaultOptions
                ),

                Question(
                    "Considero que mi calidad de vida es buena.",
                    defaultOptions
                )
            )

            "STRESS" -> listOf(

                Question(
                    "Mantengo la calma ante situaciones difíciles.",
                    defaultOptions
                ),

                Question(
                    "Encuentro momentos para relajarme durante el día.",
                    defaultOptions
                ),

                Question(
                    "Me siento capaz de manejar mis responsabilidades.",
                    defaultOptions
                ),

                Question(
                    "Puedo controlar mis preocupaciones adecuadamente.",
                    defaultOptions
                ),

                Question(
                    "Me recupero rápidamente después de situaciones estresantes.",
                    defaultOptions
                ),

                Question(
                    "Puedo organizar mis actividades sin sentirme abrumado.",
                    defaultOptions
                ),

                Question(
                    "Mantengo una actitud positiva frente a los problemas.",
                    defaultOptions
                ),

                Question(
                    "Dispongo de tiempo suficiente para descansar.",
                    defaultOptions
                ),

                Question(
                    "Logro equilibrar mis responsabilidades y mi tiempo personal.",
                    defaultOptions
                ),

                Question(
                    "Me siento en control de las situaciones importantes de mi vida.",
                    defaultOptions
                )
            )

            "SLEEP" -> listOf(

                Question(
                    "Duermo las horas necesarias para descansar.",
                    defaultOptions
                ),

                Question(
                    "Me despierto sintiéndome descansado.",
                    defaultOptions
                ),

                Question(
                    "Consigo dormir con facilidad.",
                    defaultOptions
                ),

                Question(
                    "Mi sueño suele ser reparador.",
                    defaultOptions
                ),

                Question(
                    "Mantengo horarios regulares para dormir.",
                    defaultOptions
                ),

                Question(
                    "Me despierto durante la noche.",
                    defaultOptions
                ),

                Question(
                    "Me siento con energía al comenzar el día.",
                    defaultOptions
                ),

                Question(
                    "Puedo descansar sin interrupciones importantes.",
                    defaultOptions
                ),

                Question(
                    "Evito actividades que afectan negativamente mi sueño antes de dormir.",
                    defaultOptions
                ),

                Question(
                    "Considero que mi calidad de sueño es buena.",
                    defaultOptions
                )
            )

            "SELF_ESTEEM" -> listOf(

                Question(
                    "Confío en mis capacidades.",
                    defaultOptions
                ),

                Question(
                    "Me siento valioso como persona.",
                    defaultOptions
                ),

                Question(
                    "Me siento satisfecho con quien soy.",
                    defaultOptions
                ),

                Question(
                    "Reconozco mis cualidades y fortalezas.",
                    defaultOptions
                ),

                Question(
                    "Acepto mis errores sin desvalorizarme.",
                    defaultOptions
                ),

                Question(
                    "Me siento capaz de alcanzar mis objetivos.",
                    defaultOptions
                ),

                Question(
                    "Expreso mis opiniones con seguridad.",
                    defaultOptions
                ),

                Question(
                    "Reconozco mis logros y esfuerzos.",
                    defaultOptions
                ),

                Question(
                    "Me trato con respeto y comprensión.",
                    defaultOptions
                ),

                Question(
                    "Considero que tengo cualidades positivas importantes.",
                    defaultOptions
                )
            )

            "RELATIONSHIPS" -> listOf(

                Question(
                    "Me siento escuchado por las personas cercanas.",
                    defaultOptions
                ),

                Question(
                    "Mantengo relaciones saludables con otras personas.",
                    defaultOptions
                ),

                Question(
                    "Puedo expresar mis emociones con confianza.",
                    defaultOptions
                ),

                Question(
                    "Recibo apoyo cuando lo necesito.",
                    defaultOptions
                ),

                Question(
                    "Me siento valorado por las personas importantes para mí.",
                    defaultOptions
                ),

                Question(
                    "Tengo personas en quienes puedo confiar.",
                    defaultOptions
                ),

                Question(
                    "Resuelvo los conflictos de manera respetuosa.",
                    defaultOptions
                ),

                Question(
                    "Disfruto compartir tiempo con familiares o amigos.",
                    defaultOptions
                ),

                Question(
                    "Siento que pertenezco a un grupo o comunidad.",
                    defaultOptions
                ),

                Question(
                    "Mantengo una comunicación abierta con las personas cercanas.",
                    defaultOptions
                )
            )

            else -> listOf(

                Question(
                    "Me siento optimista respecto al futuro.",
                    defaultOptions
                ),

                Question(
                    "Disfruto compartir tiempo con otras personas.",
                    defaultOptions
                ),

                Question(
                    "Me siento motivado durante el día.",
                    defaultOptions
                ),

                Question(
                    "Tengo pensamientos positivos acerca de mí mismo.",
                    defaultOptions
                ),

                Question(
                    "Me siento emocionalmente estable.",
                    defaultOptions
                ),

                Question(
                    "Encuentro motivos para sentirme agradecido(a).",
                    defaultOptions
                ),

                Question(
                    "Disfruto las actividades que realizo diariamente.",
                    defaultOptions
                ),

                Question(
                    "Me siento capaz de afrontar los desafíos cotidianos.",
                    defaultOptions
                ),

                Question(
                    "Suelo experimentar emociones agradables durante la semana.",
                    defaultOptions
                ),

                Question(
                    "Me siento satisfecho(a) con la forma en que transcurre mi vida actualmente.",
                    defaultOptions
                )
            )
        }
    }
}