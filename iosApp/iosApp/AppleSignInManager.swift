import Foundation
import AuthenticationServices
import CryptoKit
import UIKit

/// Apple Sign-In 結果資料模型
@objcMembers
public class AppleSignInResult: NSObject {
    public let idToken: String
    public let rawNonce: String
    public let email: String?
    public let fullName: PersonNameComponents?

    public init(idToken: String, rawNonce: String, email: String? = nil, fullName: PersonNameComponents? = nil) {
        self.idToken = idToken
        self.rawNonce = rawNonce
        self.email = email
        self.fullName = fullName
    }
}

/// Apple Sign-In 管理器
/// 負責處理原生 Apple Sign-In 流程
/// Firebase 登入由 Kotlin 層處理
@objcMembers
public class AppleSignInManager: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {

    // Singleton 方便從 Kotlin 呼叫
    public static let shared = AppleSignInManager()

    private var completion: ((AppleSignInResult?, NSError?) -> Void)?
    private var currentNonce: String?

    /// Apple Sign-In 流程（返回 token 和 nonce 給 Kotlin 層處理 Firebase 登入）
    public func signIn(completion: @escaping (AppleSignInResult?, NSError?) -> Void) {
        self.completion = completion

        let nonce = Self.randomNonceString()
        self.currentNonce = nonce

        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]
        request.nonce = Self.sha256(nonce)

        let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = self
        controller.presentationContextProvider = self
        controller.performRequests()
    }

    // MARK: ASAuthorizationControllerDelegate
    public func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        guard
            let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential,
            let identityToken = appleIDCredential.identityToken,
            let idTokenString = String(data: identityToken, encoding: .utf8),
            let nonce = currentNonce
        else {
            let err = NSError(domain: "AppleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid Apple ID credential or token"])
            completion?(nil, err)
            completion = nil
            return
        }

        // 返回資訊給 Kotlin 層處理 Firebase 登入
        let result = AppleSignInResult(
            idToken: idTokenString,
            rawNonce: nonce,
            email: appleIDCredential.email,
            fullName: appleIDCredential.fullName
        )
        completion?(result, nil)
        completion = nil
    }

    public func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        completion?(nil, error as NSError)
        completion = nil
    }

    // MARK: ASAuthorizationControllerPresentationContextProviding
    public func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        // 嘗試找目前最上層 window
        if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = scene.windows.first(where: { $0.isKeyWindow }) {
            return window
        }
        // 後備：建立一個臨時 window（極端情境）
        let temp = UIWindow(frame: UIScreen.main.bounds)
        temp.makeKeyAndVisible()
        return temp
    }

    // MARK: Nonce helpers (Firebase 官方建議做法)
    private static func randomNonceString(length: Int = 32) -> String {
        precondition(length > 0)
        let charset: [Character] =
            Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
        var result = ""
        var remainingLength = length

        while remainingLength > 0 {
            var randoms = [UInt8](repeating: 0, count: 16)
            let status = SecRandomCopyBytes(kSecRandomDefault, randoms.count, &randoms)
            if status != errSecSuccess {
                fatalError("Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(status)")
            }

            randoms.forEach { random in
                if remainingLength == 0 {
                    return
                }
                if random < charset.count {
                    result.append(charset[Int(random)])
                    remainingLength -= 1
                }
            }
        }
        return result
    }

    private static func sha256(_ input: String) -> String {
        let inputData = Data(input.utf8)
        let hashed = SHA256.hash(data: inputData)
        return hashed.compactMap {
            String(format: "%02x", $0)
        }
        .joined()
    }
}
