package com.qualcomm.ftcdriverstation;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PracticeTimerManager {
   private static final int AUTO_END_LENGTH = 3000;
   private static final int AUTO_LENGTH = 30000;
   private static final int DRIVER_CONTROL_LENGTH = 120000;
   private static final int ENDGAME_LENGTH = 30000;
   private static final int END_MATCH_BUZZER_LENGTH = 2000;
   private static final int GAME_ANNOUNCER_LENGTH = 5275;
   private static final int MATCH_LENGTH = 150000;
   private static final int OFFICIAL_TIMER_OFFSET = 1000;
   private static final int PICK_UP_CTRLS_LENGTH = 5000;
   private static final int TELEOP_LENGTH = 90000;
   private static final int TELE_COUNTDOWN_LENGTH = 3000;
   private static final int TIMER_TICK_PERIOD = 100;
   private int PLAYING_SOUND_STREAM_ID = -1;
   private int SOUND_ID_ABORT_MATCH;
   private int SOUND_ID_END_AUTO;
   private int SOUND_ID_END_MATCH;
   private int SOUND_ID_FACTWHISTLE;
   private int SOUND_ID_FIREBELL;
   private int SOUND_ID_MC_BEGIN_AUTO;
   private int SOUND_ID_PICK_UP_CTRLS;
   private int SOUND_ID_START_AUTO;
   private int SOUND_ID_TELE_COUNTDOWN;
   private CountDownTimer countDownTimer;
   private boolean running = false;
   private SoundPool soundPool;
   private ImageView startStopBtn;
   private final Object syncobj = new Object();
   private Context theContext;
   private TextView timerView;

   public PracticeTimerManager(Context var1, ImageView var2, TextView var3) {
      this.theContext = var1;
      this.startStopBtn = var2;
      this.timerView = var3;
      var2.setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            Object var2 = PracticeTimerManager.this.syncobj;
            synchronized(var2){}

            Throwable var10000;
            boolean var10001;
            label385: {
               label384: {
                  try {
                     if (!PracticeTimerManager.this.running) {
                        PracticeTimerManager.this.showStartDialog(PracticeTimerManager.this.theContext);
                        break label384;
                     }
                  } catch (Throwable var44) {
                     var10000 = var44;
                     var10001 = false;
                     break label385;
                  }

                  try {
                     if (PracticeTimerManager.this.countDownTimer != null) {
                        PracticeTimerManager.this.countDownTimer.cancel();
                     }
                  } catch (Throwable var42) {
                     var10000 = var42;
                     var10001 = false;
                     break label385;
                  }

                  try {
                     PracticeTimerManager.this.running = false;
                     if (PracticeTimerManager.this.PLAYING_SOUND_STREAM_ID != -1) {
                        PracticeTimerManager.this.soundPool.stop(PracticeTimerManager.this.PLAYING_SOUND_STREAM_ID);
                     }
                  } catch (Throwable var43) {
                     var10000 = var43;
                     var10001 = false;
                     break label385;
                  }

                  try {
                     PracticeTimerManager.this.playSound(PracticeTimerManager.this.SOUND_ID_ABORT_MATCH);
                     PracticeTimerManager.this.resetUi();
                  } catch (Throwable var41) {
                     var10000 = var41;
                     var10001 = false;
                     break label385;
                  }
               }

               label361:
               try {
                  return;
               } catch (Throwable var40) {
                  var10000 = var40;
                  var10001 = false;
                  break label361;
               }
            }

            while(true) {
               Throwable var45 = var10000;

               try {
                  throw var45;
               } catch (Throwable var39) {
                  var10000 = var39;
                  var10001 = false;
                  continue;
               }
            }
         }
      });
      SoundPool var4 = new SoundPool(9, 3, 0);
      this.soundPool = var4;
      this.SOUND_ID_PICK_UP_CTRLS = var4.load(var1, 2131558415, 1);
      this.SOUND_ID_TELE_COUNTDOWN = this.soundPool.load(var1, R.raw.lady_3_2_1, 1);
      this.SOUND_ID_FIREBELL = this.soundPool.load(var1, R.raw.firebell, 1);
      this.SOUND_ID_FACTWHISTLE = this.soundPool.load(var1, R.raw.factwhistle, 1);
      this.SOUND_ID_END_MATCH = this.soundPool.load(var1, R.raw.endmatch, 1);
      this.SOUND_ID_ABORT_MATCH = this.soundPool.load(var1, R.raw.fogblast, 1);
      this.SOUND_ID_START_AUTO = this.soundPool.load(var1, R.raw.charge, 1);
      this.SOUND_ID_END_AUTO = this.soundPool.load(var1, R.raw.endauto, 1);
      this.SOUND_ID_MC_BEGIN_AUTO = this.soundPool.load(var1, R.raw.mc_begin_match, 1);
   }

   private void autoEndTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_END_AUTO);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_white));
            this.timerView.setText(this.formatTimeLeft(120000L));
            CountDownTimer var15 = new CountDownTimer(3000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.pickUpControllersTimer();
               }

               public void onTick(long var1) {
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private void autoTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_START_AUTO);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_white));
            CountDownTimer var15 = new CountDownTimer(30000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.autoEndTimer();
               }

               public void onTick(long var1) {
                  PracticeTimerManager.this.timerView.setText(PracticeTimerManager.this.formatTimeLeft(var1 + 1000L + 120000L));
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private void endMatchTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_END_MATCH);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_white));
            this.timerView.setText(this.formatTimeLeft(0L));
            CountDownTimer var15 = new CountDownTimer(2000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.timerView.setText(PracticeTimerManager.this.formatTimeLeft(0L));
                  PracticeTimerManager.this.running = false;
                  PracticeTimerManager.this.resetUi();
               }

               public void onTick(long var1) {
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private void endgameTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_FACTWHISTLE);
            CountDownTimer var15 = new CountDownTimer(30000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.endMatchTimer();
               }

               public void onTick(long var1) {
                  PracticeTimerManager.this.timerView.setText(PracticeTimerManager.this.formatTimeLeft(var1 + 1000L));
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private String formatSeconds(int var1) {
      if (var1 < 10) {
         StringBuilder var2 = new StringBuilder();
         var2.append("0");
         var2.append(var1);
         return var2.toString();
      } else {
         return Integer.toString(var1);
      }
   }

   private String formatTimeLeft(long var1) {
      int var3 = (int)(var1 / 1000L);
      int var4 = (int)(var1 / 60000L % 60L);
      StringBuilder var5 = new StringBuilder();
      var5.append(var4);
      var5.append(":");
      var5.append(this.formatSeconds(var3 % 60));
      return var5.toString();
   }

   private void gameAnnouncerTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_MC_BEGIN_AUTO);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_yellow));
            this.timerView.setText(this.formatTimeLeft(150000L));
            CountDownTimer var15 = new CountDownTimer(5275L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.autoTimer();
               }

               public void onTick(long var1) {
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private void pickUpControllersTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_PICK_UP_CTRLS);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_yellow));
            CountDownTimer var15 = new CountDownTimer(5000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.teleCountDownTimer();
               }

               public void onTick(long var1) {
                  PracticeTimerManager.this.timerView.setText(PracticeTimerManager.this.formatTimeLeft(var1 + 1000L));
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private void playSound(int var1) {
      this.PLAYING_SOUND_STREAM_ID = this.soundPool.play(var1, 1.0F, 1.0F, 1, 0, 1.0F);
   }

   private void teleCountDownTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_TELE_COUNTDOWN);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_red));
            CountDownTimer var15 = new CountDownTimer(3000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.teleopTimer();
               }

               public void onTick(long var1) {
                  PracticeTimerManager.this.timerView.setText(PracticeTimerManager.this.formatTimeLeft(var1 + 1000L));
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   private void teleopTimer() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label123: {
         try {
            if (!this.running) {
               return;
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label123;
         }

         label117:
         try {
            this.playSound(this.SOUND_ID_FIREBELL);
            this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_white));
            CountDownTimer var15 = new CountDownTimer(90000L, 100L) {
               public void onFinish() {
                  PracticeTimerManager.this.endgameTimer();
               }

               public void onTick(long var1) {
                  PracticeTimerManager.this.timerView.setText(PracticeTimerManager.this.formatTimeLeft(var1 + 1000L + 30000L));
               }
            };
            this.countDownTimer = var15.start();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label117;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   public void reset() {
      Object var1 = this.syncobj;
      synchronized(var1){}

      Throwable var10000;
      boolean var10001;
      label122: {
         try {
            if (this.countDownTimer != null) {
               this.countDownTimer.cancel();
            }
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label122;
         }

         label119:
         try {
            this.running = false;
            this.soundPool.stop(this.SOUND_ID_PICK_UP_CTRLS);
            this.soundPool.stop(this.SOUND_ID_TELE_COUNTDOWN);
            this.soundPool.stop(this.SOUND_ID_FIREBELL);
            this.soundPool.stop(this.SOUND_ID_FACTWHISTLE);
            this.soundPool.stop(this.SOUND_ID_END_MATCH);
            this.soundPool.stop(this.SOUND_ID_ABORT_MATCH);
            this.soundPool.stop(this.SOUND_ID_START_AUTO);
            this.soundPool.stop(this.SOUND_ID_END_AUTO);
            this.soundPool.stop(this.SOUND_ID_MC_BEGIN_AUTO);
            this.resetUi();
            return;
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label119;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var12) {
            var10000 = var12;
            var10001 = false;
            continue;
         }
      }
   }

   void resetUi() {
      this.timerView.setTextColor(this.theContext.getResources().getColor(R.color.practice_timer_font_white));
      this.timerView.setText(this.formatTimeLeft(150000L));
      this.startStopBtn.setImageDrawable(this.theContext.getResources().getDrawable(R.drawable.ic_play_circle_filled_24dp));
   }

   void showStartDialog(Context var1) {
      Builder var2 = new Builder(var1);
      var2.setTitle("Start from...");
      android.content.DialogInterface.OnClickListener var3 = new android.content.DialogInterface.OnClickListener() {
         public void onClick(DialogInterface var1, int var2) {
            PracticeTimerManager.this.running = true;
            PracticeTimerManager.this.startStopBtn.setImageDrawable(PracticeTimerManager.this.theContext.getResources().getDrawable(R.drawable.ic_stop_24dp));
            if (var2 != 0) {
               if (var2 != 1) {
                  if (var2 != 2) {
                     if (var2 == 3) {
                        PracticeTimerManager.this.endgameTimer();
                     }
                  } else {
                     PracticeTimerManager.this.teleopTimer();
                  }
               } else {
                  PracticeTimerManager.this.pickUpControllersTimer();
               }
            } else {
               PracticeTimerManager.this.gameAnnouncerTimer();
            }

         }
      };
      var2.setItems(new String[]{"Autonomous", "Auto --> Tele-Op Transition", "Tele-Op", "Endgame"}, var3);
      var2.create().show();
   }
}
