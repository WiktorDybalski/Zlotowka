import Navigation from "@/components/navigation/Navigation";
import { EnterWithAuth } from "@/components/providers/AuthProvider";

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <EnterWithAuth>
      <div className="flex w-full flex-col font-semibold text-background dark:bg-dark dark:text-background xl:flex-row">
        <div className="w-full min-h-20 left-0 top-0 xl:w-96 xl:h-screen xl:sticky">
          <Navigation />
        </div>
        <div className="w-full text-accent dark:text-background">
          {children}
        </div>
      </div>
    </EnterWithAuth>
  );
}
