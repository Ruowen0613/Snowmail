package ca.uwaterloo.service

import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun startHttpCallbackServer(onUrlReceived: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        println("Starting HTTP Callback Server...")
        embeddedServer(Netty, port = 8080) {
            routing {
                get("/auth-callback") {
                    val fragmentHtml = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Redirecting...</title>
                    </head>
                    <body>
                    <p>Processing your request. Please wait...</p>
                    <script>
                        const fragment = window.location.hash.substring(1);
                        const queryParams = new URLSearchParams(fragment);

                        if (queryParams.has("access_token")) {
                            const accessToken = queryParams.get("access_token");
                            const refreshToken = queryParams.get("refresh_token");
                            const expiresAt = queryParams.get("expires_at");
                            const expiresIn = queryParams.get("expires_in");
                            const tokenType = queryParams.get("token_type");
                            const type = queryParams.get("type");
                            const newUrl = `${"$"}{window.location.origin}${"$"}{window.location.pathname}?access_token=${"$"}{accessToken}&expires_at=${"$"}{expiresAt}&expires_in=${"$"}{expiresIn}&refresh_token=${"$"}{refreshToken}&token_type=${"$"}{tokenType}&type=${"$"}{type}`;
                            window.location.replace(newUrl);
                        } else {
                            document.body.innerHTML = "<p>No access_token found in fragment.</p>";
                            console.error("No access_token found in fragment. Full hash:", window.location.hash);
                        }
                    </script>
                    </body>
                    </html>
                    """.trimIndent()

                    val queryParameters = call.request.queryParameters
                    val accessToken = queryParameters["access_token"]

                    if (accessToken != null) {
                        val scheme = call.request.origin.scheme // http or https
                        val host = call.request.host() // localhost
                        val port = call.request.port() // 8080
                        //change the query params to fragment
                        val fullUrlWithFragment = "$scheme://$host:$port${call.request.uri.replace("?", "#")}"
                        println("Received HTTP callback URL with query params: $fullUrlWithFragment")
                        onUrlReceived(fullUrlWithFragment)
                        call.respondText("Access token received. You can close this window.")
                    } else {
                        // Otherwise, respond with the HTML page to handle fragment conversion
                        call.respondText(fragmentHtml, contentType = ContentType.Text.Html)
                    }
                }
            }
        }.start(wait = false)
        println("HTTP Callback Server started on port 8080")
    }
}


