import com.soywiz.krypto.md5

data class Gravatar(
    val email: String?,
    val size: Int = 64,
) {
    val url: String
        get() = email?.encodeToByteArray()?.md5()?.hex
            ?.let { "https://www.gravatar.com/avatar/$it?s=$size" }
            ?: MARIO
}

const val MARIO = "data:image/gif;base64,R0lGODlhEAAQAPIAAAAAAGU1LPgcL/7xBBVg" +
    "ra6urP+7jgAAACH5BAEAAAcALAAAAAAQABAAAANJeKrSvXC5+eIJ2BhgrAoGqHGaBWaj1kHo" +
    "CLzeoc7xIRANUTv47UQTgrBXURQKt4FQKThCjoXhEPqMSqcFy1CxNWFYX3D4gwkoEgA7"

const val SAMUS = "data:image/gif;base64,R0lGODlhKAAoAPQRAAAAAAEBAQ8PDxt5W44G" +
    "D/oKGfsLGiXqNGj/b/uWM/yWM/yXNP7/P6kA9yzXn5CQkNmw+////wAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAAAAAAALAAAAAAoACgAAAX+YCSOZGme" +
    "aKqubOu+cCzPLACYdn7TJKAoOxGAYDAUjEGaL7FY7ABFAqFANSRlgCbweTROqYVrLGtzQqvU" +
    "L0E8TriBxcLhgDhQD+yxImE7yulzc0E6WDlGd3WBOgOMOSkNDTh8Ul9Gc2F7AAMOnAAOQZGh" +
    "EBCRJT42D1JUDw8FD5mMjQOgEaKkJ2QArK2rAAlAsY4okCl9Osc3hDxZClVGfDwqvgkMRwx5" +
    "0UJ7DNzXwtkjyDl70OBCfNTdANy+2GMLW+zsCk7mEdPUxwtM7jC+Ctd68AMQwNw/Gz2c7AMC" +
    "blwSVLv6tcjCZIEAU9+iCfi1T2I2fB6jZTFjrxiqkMsX4D1ASUPAjwcXS6LIKLOmzZs4c+o0" +
    "EQIAOw=="
