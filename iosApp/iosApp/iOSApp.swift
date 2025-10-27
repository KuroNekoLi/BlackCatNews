import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {

    init() {
        configureFirebase()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
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
}