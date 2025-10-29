import SwiftUI
import FirebaseCore
import GoogleSignIn

@main
struct iOSApp: App {

    init() {
        configureFirebase()
        configureGoogleSignIn()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }

    private func configureFirebase() {
        #if DEBUG
        // Debug build: 使用 debug 配置
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info-Debug", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
        }
        #else
        // Release build: 使用 production 配置
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info-Release", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
        }
        #endif
    }

    private func configureGoogleSignIn() {
        // 等待 Firebase 配置完成後，從 FirebaseApp 取得 clientID
        guard let app = FirebaseApp.app(),
              let clientID = app.options.clientID
        else {
            print("Warning: Firebase app not configured or clientID not found")
            return
        }

        // 使用 Firebase 的 clientID 來配置 Google Sign-In
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)

        print("Google Sign-In configured with clientID: \(clientID)")
    }
}