import Link from "next/link";

export default function SettingsLink({ onLinkClick }: { onLinkClick?: () => void }) {
  return (
    <Link href="/settings" onClick={onLinkClick}>
      <div className="flex items-center hover:text-neutral-300 cursor-pointer transition-colors">
        <span className="material-symbols text-xl font-light">settings</span>
        <div>
          <p className="ml-3 text-xl">Ustawienia</p>
        </div>
      </div>
    </Link>
  );
}
