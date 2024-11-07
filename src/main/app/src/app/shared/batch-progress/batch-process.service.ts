import { Injectable, computed, effect, signal } from "@angular/core";
import { UtilService } from "src/app/core/service/util.service";
import { ObjectType } from "src/app/core/websocket/model/object-type";
import { Opcode } from "src/app/core/websocket/model/opcode";
import { WebsocketListener } from "src/app/core/websocket/model/websocket-listener";
import { WebsocketService } from "src/app/core/websocket/websocket.service";
import { ScreenEvent } from "../../core/websocket/model/screen-event";

type SubscriptionType = {
  opcode: Opcode;
  objectType: ObjectType;
};

const DEFAULT_PROCESS_TIMEOUT_IN_MS = 1000 * 30;

type ProcessTimeout = {
  durationInMs?: number;
  onTimeout: () => void;
};

type Options = {
  ids: string[];
  progressSubscription: SubscriptionType & {
    onNotification?: (id: string, event: ScreenEvent) => void;
  };
  processTimeout?: ProcessTimeout;
  finalSubscription?: SubscriptionType & { screenEventResourceId: string };
  finally: () => void | Promise<void>;
};

@Injectable({
  providedIn: "root",
})
export class BatchProcessService {
  private state = signal<Record<string, boolean>>({});
  private values = computed(() => Object.values(this.state()));
  private progress = computed(() => {
    const v = this.values();
    return v.length
      ? Math.round((v.filter((done) => done).length / v.length) * 100)
      : undefined;
  });
  private subscriptions: WebsocketListener[] = [];
  private options: Options;
  private timeout?: ReturnType<typeof setTimeout>;

  constructor(
    private websocketService: WebsocketService,
    private utilService: UtilService,
  ) {
    effect(() => {
      if (this.progress() === 100 && !this.options.finalSubscription) {
        Promise.resolve(this.options.finally()).finally(() => this.stop());
      }
    });
  }

  start(options: Options) {
    this.stop();
    this.options = options;
    this.state.set(Object.fromEntries(options.ids.map((x) => [x, false])));
    this.subscriptions = options.ids.map((id) => {
      const subscription = this.websocketService.addListener(
        options.progressSubscription.opcode,
        options.progressSubscription.objectType,
        id,
        (event) => {
          this.clearAndSetTimeout(options.processTimeout);
          this.removeSubscription(subscription);
          this.state.update((state) => ({
            ...state,
            [id]: true,
          }));
          options.progressSubscription.onNotification?.(id, event);
        },
      );
      return subscription;
    });
    if (options.finalSubscription) {
      const finalSubscription = this.websocketService.addListener(
        options.finalSubscription.opcode,
        options.finalSubscription.objectType,
        options.finalSubscription.screenEventResourceId,
        () =>
          Promise.resolve(this.options.finally()).finally(() => this.stop()),
      );
      this.subscriptions.push(finalSubscription);
    }
    this.clearAndSetTimeout(options.processTimeout);
  }

  showProgress(message: string) {
    this.utilService.openProgressDialog({
      progressPercentage: this.progress,
      message,
    });
  }

  stop() {
    clearTimeout(this.timeout);
    this.utilService.closeProgressDialog();
    this.clearSubscriptions();
  }

  update(ids: string[]) {
    this.state.update((state) => ({
      ...state,
      ...Object.fromEntries(ids.map((x) => [x, true])),
    }));
  }

  private clearSubscriptions() {
    this.websocketService.removeListeners(this.subscriptions);
    this.subscriptions = [];
  }

  private clearAndSetTimeout(options?: ProcessTimeout) {
    clearTimeout(this.timeout);

    if (!options) return;

    this.timeout = setTimeout(() => {
      options.onTimeout();
      Promise.resolve(this.options.finally()).finally(() => this.stop());
    }, options.durationInMs ?? DEFAULT_PROCESS_TIMEOUT_IN_MS);
  }

  private removeSubscription(subscription: WebsocketListener) {
    const i = this.subscriptions.indexOf(subscription);
    if (i !== -1) this.subscriptions.splice(i, 1);
    this.websocketService.removeListener(subscription);
  }
}
