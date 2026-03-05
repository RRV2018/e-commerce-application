import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./css/Login.css";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem("token");
    if (token) {
      navigate("/dashboard", { replace: true });
    }
  }, [navigate]);

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

    setIsSubmitting(true);
    try {
      await api.post("/api/user/register", {
        name: name.trim(),
        email: email.trim(),
        password,
      });
      navigate("/login", { replace: true, state: { message: "Account created. Please sign in." } });
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        (err.response?.data?.errors && Object.values(err.response.data.errors).join(" ")) ||
        "Registration failed. Please try again.";
      setError(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <header className="login-header">
          <h1 className="login-title">Create account</h1>
          <p className="login-subtitle">Sign up to get started</p>
        </header>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="login-field">
            <label htmlFor="register-name">Name</label>
            <input
              id="register-name"
              className="login-input"
              type="text"
              placeholder="Full name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoComplete="name"
              disabled={isSubmitting}
            />
          </div>
          <div className="login-field">
            <label htmlFor="register-email">Email</label>
            <input
              id="register-email"
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
          <div className="login-field">
            <label htmlFor="register-password">Password</label>
            <input
              id="register-password"
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
            <label htmlFor="register-confirm">Confirm password</label>
            <input
              id="register-confirm"
              className="login-input"
              type="password"
              placeholder="Repeat password"
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
            {isSubmitting ? "Creating account…" : "Sign up"}
          </button>
        </form>

        <footer className="login-footer">
          Already have an account? <Link to="/login">Sign in</Link>
        </footer>
      </div>
    </div>
  );
}

export default Register;
