export interface ProgressBarProps {
  progress: number; // wartość od 0 do 1
}

export const ProgressBar = ({ progress }: ProgressBarProps) => {
  const percentage = Math.min(Math.max(progress, 0), 1) * 100;
  return (
    <div className="w-full h-5 bg-neutral-200 dark:bg-accent rounded-xl mt-4">
      <div
        className="h-full bg-accent dark:bg-backgroundLightDark rounded-xl"
        style={{ width: `${percentage}%` }}
      ></div>
    </div>
  );
};
