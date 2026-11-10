type AuthSubmitButtonProps = {
  isSubmitting: boolean;
  loadingText: string;
  defaultText: string;
};

export default function AuthSubmitButton({
  isSubmitting,
  loadingText,
  defaultText,
}: AuthSubmitButtonProps) {
  return (
    <button type="submit" className="button auth-button" disabled={isSubmitting}>
      <span className="auth-button__text">
        {isSubmitting ? loadingText : defaultText}
      </span>
    </button>
  );
}