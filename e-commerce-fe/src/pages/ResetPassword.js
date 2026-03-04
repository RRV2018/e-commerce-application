import { useState, useEffect } from "react";
import { Link, useSearchParams } from "react-router-dom";
import api from "../api/axios";
import "./css/Login.css";

function ResetPassword() {
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get("token") || "";

  const [token, setToken] = useState(tokenFromUrl);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    setToken(tokenFromUrl);
  }, [tokenFromUrl]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }
    if (!token.trim()) {
      setError("Invalid reset link. Please request a new one.");
      return;
    }

    setIsSubmitting(true);
    try {
      await api.post("/api/auth/reset-password", {
        token: token.trim(),
        newPassword: password,
      });
      setSuccess(true);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Invalid or expired link. Please request a new reset link."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  if (success) {
    return (
      <div className="login-page">
        <div className="login-card">
          <header className="login-header">
            <h1 className="login-title">Password reset</h1>
            <p className="login-subtitle">
              Your password has been updated. You can now sign in.
            </p>
          </header>
          <footer className="login-footer">
            <Link to="/login" className="login-submit" style={{ display: "inline-block", textDecoration: "none", textAlign: "center" }}>
              Sign in
            </Link>
          </footer>
        </div>
      </div>
    );
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <header className="login-header">
          <h1 className="login-title">Set new password</h1>
          <p className="login-subtitle">
            Enter your new password below.
          </p>
        </header>

        <form className="login-form" onSubmit={handleSubmit}>
          {!tokenFromUrl && (
            <div className="login-field">
              <label htmlFor="reset-token">Reset token</label>
              <input
                id="reset-token"
                className="login-input"
                type="text"
                placeholder="Paste the token from your email"
                value={token}
                onChange={(e) => setToken(e.target.value)}
                required
                disabled={isSubmitting}
              />
            </div>
          )}
          <div className="login-field">
            <label htmlFor="reset-password">New password</label>
            <input
              id="reset-password"
              className="login-input"
              type="password"
              placeholder="At least 6 characters"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
              autoComplete="new-password"
              disabled={isSubmitting}
            />
          </div>
          <div className="login-field">
            <label htmlFor="reset-confirm">Confirm password</label>
            <input
              id="reset-confirm"
              className="login-input"
              type="password"
              placeholder="Repeat new password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              minLength={6}
              autoComplete="new-password"
              disabled={isSubmitting}
            />
          </div>

          {error && (
            <div className="login-error" role="alert">
              {error}
            </div>
          )}
          <button
            className="login-submit"
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Updating…" : "Update password"}
          </button>
        </form>

        <footer className="login-footer">
          <Link to="/forgot-password">Request new link</Link>
          <span style={{ margin: "0 8px" }}>{" | "}</span>
          <Link to="/login">Back to sign in</Link>
        </footer>
      </div>
    </div>
  );
}

export default ResetPassword;
