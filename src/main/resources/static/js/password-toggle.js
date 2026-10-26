document.querySelectorAll("[data-password-toggle]").forEach((button) => {
    const passwordInput = document.getElementById(button.dataset.passwordToggle);
    if (!passwordInput) {
        return;
    }

    button.addEventListener("click", () => {
        const passwordIsVisible = passwordInput.type === "text";
        passwordInput.type = passwordIsVisible ? "password" : "text";
        button.textContent = passwordIsVisible ? button.dataset.showLabel : button.dataset.hideLabel;
        button.setAttribute("aria-pressed", String(!passwordIsVisible));
    });
});
