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
        }
    }

    private func configureFirebase() {
        #if DEBUG
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info-Debug", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
        }
        #else
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info-Release", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
        }
        #endif
    }

    private func configureGoogleSignIn() {
        guard let clientID = getClientID() else {
            print("Warning: Cannot read CLIENT_ID from GoogleService-Info.plist")
            return
        }

        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config

        print("Google Sign-In configured successfully")
    }

    private func getClientID() -> String? {
        #if DEBUG
        if let path = Bundle.main.path(forResource: "GoogleService-Info-Debug", ofType: "plist"),
           let plist = NSDictionary(contentsOfFile: path),
           let clientID = plist["CLIENT_ID"] as? String {
            return clientID
        }
        #else
        if let path = Bundle.main.path(forResource: "GoogleService-Info-Release", ofType: "plist"),
           let plist = NSDictionary(contentsOfFile: path),
           let clientID = plist["CLIENT_ID"] as? String {
            return clientID
        }
        #endif
        return nil
    }
}