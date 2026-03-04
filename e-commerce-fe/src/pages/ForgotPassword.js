import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import "./css/Login.css";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess(null);
    setIsSubmitting(true);

    try {
      const res = await api.post("/api/auth/forgot-password", { email });
      setSuccess(res.data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Something went wrong. Please try again."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <header className="login-header">
          <h1 className="login-title">Forgot password</h1>
          <p className="login-subtitle">
            Enter your email and we will send you a link to reset your password.
          </p>
        </header>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="login-field">
            <label htmlFor="forgot-email">Email</label>
            <input
              id="forgot-email"
              className="login-input"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
              disabled={isSubmitting}
            />
          </div>

          {error && (
            <div className="login-error" role="alert">
              {error}
            </div>
          )}
          {success && (
            <div className="login-success" role="status">
              <p>{success.message}</p>
              {success.resetLink && (
                <p className="login-reset-link">
                  <strong>Reset link (use within 1 hour):</strong>
                  <br />
                  <a href={success.resetLink} rel="noopener noreferrer">
                    {success.resetLink}
                  </a>
                </p>
              )}
            </div>
          )}
          <button
            className="login-submit"
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Sending…" : "Send reset link"}
          </button>
        </form>

        <footer className="login-footer">
          <Link to="/login">Back to sign in</Link>
        </footer>
      </div>
    </div>
  );
}

export default ForgotPassword;
