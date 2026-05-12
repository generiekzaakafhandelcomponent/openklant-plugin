import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import {
  FunctionConfigurationComponent,
  FunctionConfigurationData,
  PluginTranslatePipeModule,
} from "@valtimo/plugin";
import {
  BehaviorSubject,
  combineLatest,
  Observable, ReplaySubject,
  Subscription,
  take,
} from "rxjs";
import {AsyncPipe, NgIf} from "@angular/common";
import {FormModule, FormOutput, InputModule, TooltipModule} from "@valtimo/components";
import {GetContactMomentsByBsnConfig} from "../../models/get-contact-moments-by-bsn-config";

@Component({
  selector: "get-contact-moments-by-bsn",
  standalone: true,
  imports: [
    AsyncPipe,
    FormModule,
    InputModule,
    NgIf,
    PluginTranslatePipeModule,
    TooltipModule,
  ],
  templateUrl: "./get-contact-moments-by-bsn.component.html"
})
export class GetContactMomentsByBsnComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$?: Observable<GetContactMomentsByBsnConfig>;
  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<FunctionConfigurationData> =
    new EventEmitter<FunctionConfigurationData>();

  private saveSubscription!: Subscription;

  private readonly config$ =
    new BehaviorSubject<GetContactMomentsByBsnConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as GetContactMomentsByBsnConfig);
    this.handleValid(formOutput as GetContactMomentsByBsnConfig);
  }

  private handleValid(formOutput: GetContactMomentsByBsnConfig): void {
    const valid = !!formOutput.resultPvName && !!formOutput.bsn;

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe((save) => {
      combineLatest([this.config$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid && config) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
